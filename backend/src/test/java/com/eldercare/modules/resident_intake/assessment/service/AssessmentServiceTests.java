package com.eldercare.modules.resident_intake.assessment.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;
import com.eldercare.modules.resident_intake.admission_ledger.repository.AdmissionRepository;
import com.eldercare.modules.resident_intake.assessment.AssessmentEntity;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentCreateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentDecisionRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentUpdateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.response.AssessmentResponse;
import com.eldercare.modules.resident_intake.assessment.repository.AssessmentRepository;
import com.eldercare.modules.resident_intake.assessment.service.impl.AssessmentServiceImpl;
import com.eldercare.modules.resident_intake.assessment_detail.dto.request.AssessmentDetailRequest;
import com.eldercare.modules.resident_intake.assessment_metric.repository.AssessmentMetricRepository;
import com.eldercare.modules.admin.facility_setup.care_level.entity.CareLevelEntity;
import com.eldercare.modules.admin.facility_setup.care_level.repository.CareLevelRepository;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.LocClassificationResultResponse;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

@ExtendWith(MockitoExtension.class)
public class AssessmentServiceTests {
  @Mock
  private AssessmentRepository assessRepo;
  @Mock
  private AssessmentMetricRepository metricRepo;
  @Mock
  private AdmissionRepository admRepo;
  @Mock
  private CareLevelRepository careLevelRepo;
  @InjectMocks
  private AssessmentServiceImpl service;

  private AdmissionEntity buildAdmission(LocalDate admissionDate, boolean current, LocalDate dischargeDate) {
    AdmissionEntity adm = new AdmissionEntity();
    adm.setId(1L);
    adm.setIsCurrent(current);
    adm.setDischargeDate(dischargeDate);
    adm.setAdmissionDate(admissionDate);
    ResidentEntity r = new ResidentEntity();
    r.setId(1L);
    r.setFirstName("A");
    r.setLastName("B");
    adm.setResident(r);
    return adm;
  }

  @Test // admission does not exist -> throws exception
  void create_admissionNotFound_throwsException() {
    when(admRepo.findById(1L)).thenReturn(Optional.empty());
    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of());

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // admission is not current -> throws exception
  void create_admissionNotCurrent_throwsException() {
    AdmissionEntity adm = buildAdmission(LocalDate.now(), false, null);
    when(admRepo.findById(1L)).thenReturn(Optional.of(adm));

    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of());

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // admission is already discharged -> throws exception
  void create_admissionDischarged_throwsException() {
    AdmissionEntity adm = buildAdmission(LocalDate.now(), true, LocalDate.now());
    when(admRepo.findById(1L)).thenReturn(Optional.of(adm));

    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of());

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // admissionDate is exactly 14 days ago -> still valid (inner boundary)
  void create_exactly14DaysAfterAdmission_isValid() {
    AdmissionEntity adm = buildAdmission(LocalDate.now().minusDays(14), true, null);
    when(admRepo.findById(1L)).thenReturn(Optional.of(adm));
    when(assessRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.empty());
    when(assessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(careLevelRepo.findById(anyLong())).thenReturn(Optional.of(new CareLevelEntity()));

    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of());

    assertDoesNotThrow(() -> service.create(req));
  }

  @Test // admissionDate is 15 days ago -> expired, throws exception (outer boundary)
  void create_15DaysAfterAdmission_throwsException() {
    AdmissionEntity adm = buildAdmission(LocalDate.now().minusDays(15), true, null);
    when(admRepo.findById(1L)).thenReturn(Optional.of(adm));

    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of());

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // EQ: resident already has an active DRAFT assessment -> throws exception
  void create_existingDraftAssessment_throwsException() {
    AdmissionEntity adm = buildAdmission(LocalDate.now(), true, null);
    AssessmentEntity old = new AssessmentEntity();
    old.setStatus("DRAFT");

    when(admRepo.findById(1L)).thenReturn(Optional.of(adm));
    when(assessRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.of(old));

    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of());

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // totalScore = 49 -> level 3 (below 50 threshold)
  void create_score49_suggestsLevel3() {
    assertSuggestedLevel(49, 3L);
  }

  @Test // totalScore = 50 -> level 2 (exact lower boundary)
  void create_score50_suggestsLevel2() {
    assertSuggestedLevel(50, 2L);
  }

  @Test // totalScore = 79 -> level 2 (just below 80 threshold)
  void create_score79_suggestsLevel2() {
    assertSuggestedLevel(79, 2L);
  }

  @Test // totalScore = 80 -> level 1 (exact upper boundary)
  void create_score80_suggestsLevel1() {
    assertSuggestedLevel(80, 1L);
  }

  private void assertSuggestedLevel(int score, Long expectedLevelId) {
    AdmissionEntity adm = buildAdmission(LocalDate.now(), true, null);
    when(admRepo.findById(1L)).thenReturn(Optional.of(adm));
    when(assessRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.empty());
    when(assessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    CareLevelEntity level = new CareLevelEntity();
    level.setId(expectedLevelId);
    when(careLevelRepo.findById(expectedLevelId)).thenReturn(Optional.of(level));

    AssessmentDetailRequest d = new AssessmentDetailRequest();
    d.setMetricId(1L);
    d.setScore(score);

    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of(d));

    AssessmentResponse res = service.create(req);
    assertEquals(expectedLevelId, res.getSuggestedCareLevelId());
  }

  @Test // status is DRAFT -> updated successfully
  void update_statusDraft_updatesSuccessfully() {
    AssessmentEntity a = new AssessmentEntity();
    a.setStatus("DRAFT");
    a.setDetails(new ArrayList<>());
    a.setResident(new ResidentEntity());

    when(assessRepo.findById(1L)).thenReturn(Optional.of(a));
    when(assessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(careLevelRepo.findById(anyLong())).thenReturn(Optional.of(new CareLevelEntity()));

    AssessmentDetailRequest d = new AssessmentDetailRequest();
    d.setMetricId(1L);
    d.setScore(60);
    AssessmentUpdateRequest req = new AssessmentUpdateRequest();
    req.setDetails(List.of(d));

    AssessmentResponse res = service.update(1L, req);
    assertEquals(60, res.getAdlTotalScore());
  }

  @Test // status is not DRAFT -> throws exception, update not allowed
  void update_statusNotDraft_throwsException() {
    AssessmentEntity a = new AssessmentEntity();
    a.setStatus("COMPLETED");
    when(assessRepo.findById(1L)).thenReturn(Optional.of(a));

    AssessmentUpdateRequest req = new AssessmentUpdateRequest();
    req.setDetails(List.of());

    assertThrows(RuntimeException.class, () -> service.update(1L, req));
  }

  @Test // confirmedCareLevelId matches suggestedCareLevel -> isOverridden is false
  void decide_confirmedMatchesSuggested_notOverridden() {
    AssessmentEntity a = new AssessmentEntity();
    a.setResident(new ResidentEntity());
    a.setDetails(new ArrayList<>());
    CareLevelEntity suggested = new CareLevelEntity();
    suggested.setId(2L);
    a.setSuggestedCareLevel(suggested);

    when(assessRepo.findById(1L)).thenReturn(Optional.of(a));
    when(assessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    AssessmentDecisionRequest req = new AssessmentDecisionRequest();
    req.setStatus("COMPLETED");
    req.setConfirmedCareLevelId(2L);

    service.decide(1L, req);
    assertFalse(a.getIsOverridden());
  }

  @Test // confirmedCareLevelId differs from suggestedCareLevel -> isOverridden is true
  void decide_confirmedDiffersFromSuggested_isOverridden() {
    AssessmentEntity a = new AssessmentEntity();
    a.setResident(new ResidentEntity());
    a.setDetails(new ArrayList<>());
    CareLevelEntity suggested = new CareLevelEntity();
    suggested.setId(2L);
    a.setSuggestedCareLevel(suggested);

    when(assessRepo.findById(1L)).thenReturn(Optional.of(a));
    when(assessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    AssessmentDecisionRequest req = new AssessmentDecisionRequest();
    req.setStatus("COMPLETED");
    req.setConfirmedCareLevelId(1L);

    service.decide(1L, req);
    assertTrue(a.getIsOverridden());
  }

  @Test // confirmedCareLevelId is null -> does not set confirmedCareLevel, retains
        // previous override state
  void decide_confirmedCareLevelNull_doesNotSetConfirmedLevel() {
    AssessmentEntity a = new AssessmentEntity();
    a.setResident(new ResidentEntity());
    a.setDetails(new ArrayList<>());
    when(assessRepo.findById(1L)).thenReturn(Optional.of(a));
    when(assessRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    AssessmentDecisionRequest req = new AssessmentDecisionRequest();
    req.setStatus("REJECTED");

    service.decide(1L, req);
    assertNull(a.getConfirmedCareLevel());
  }

  @Test // resident has an active assessment -> returns full details
  void getClassificationResult_found_returnsResult() {
    AssessmentEntity a = new AssessmentEntity();
    a.setId(1L);
    a.setAdlTotalScore(60);
    a.setDetails(List.of());
    ResidentEntity r = new ResidentEntity();
    r.setId(1L);
    r.setFirstName("A");
    r.setLastName("B");
    a.setResident(r);

    when(assessRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.of(a));

    LocClassificationResultResponse res = service.getClassificationResult(1L);
    assertEquals(60, res.getAdlTotalScore());
  }

  @Test // resident has no active assessment -> throws exception
  void getClassificationResult_notFound_throwsException() {
    when(assessRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> service.getClassificationResult(1L));
  }

  @Test // status is DRAFT -> deleted successfully
  void deleteDraft_statusDraft_deletesSuccessfully() {
    AssessmentEntity a = new AssessmentEntity();
    a.setStatus("DRAFT");
    when(assessRepo.findById(1L)).thenReturn(Optional.of(a));

    service.deleteDraft(1L);
    verify(assessRepo).delete(a);
  }

  @Test // status is not DRAFT -> throws exception
  void deleteDraft_statusNotDraft_throwsException() {
    AssessmentEntity a = new AssessmentEntity();
    a.setStatus("COMPLETED");
    when(assessRepo.findById(1L)).thenReturn(Optional.of(a));

    assertThrows(RuntimeException.class, () -> service.deleteDraft(1L));
  }
}
