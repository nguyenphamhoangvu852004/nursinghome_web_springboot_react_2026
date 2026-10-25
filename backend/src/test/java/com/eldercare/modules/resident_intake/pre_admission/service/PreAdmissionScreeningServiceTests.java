package com.eldercare.modules.resident_intake.pre_admission.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eldercare.modules.resident_intake.pre_admission.PreAdmissionScreeningEntity;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreCreateRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreDecisionRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreResponse;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreSelectDTO;
import com.eldercare.modules.resident_intake.pre_admission.repository.PreAdmissionScreeningRepository;
import com.eldercare.modules.resident_intake.pre_admission.service.impl.PreAdmissionScreeningServiceImpl;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class PreAdmissionScreeningServiceTests {
  @Mock
  private PreAdmissionScreeningRepository preRepo;
  @Mock
  private ResidentRepository residentRepo;
  @InjectMocks
  private PreAdmissionScreeningServiceImpl service;

  @Test // resident does not exist -> throws exception
  void create_residentNotFound_throwsException() {
    when(residentRepo.findById(99L)).thenReturn(Optional.empty());
    PreCreateRequest req = new PreCreateRequest();

    req.setResidentId(99L);
    assertThrows(RuntimeException.class, () -> service.create(req));
    verify(preRepo, never()).save(any());
  }

  @Test // resident exists, current record is DRAFT -> throws exception
  void create_existingDraftCurrent_throwsException() {
    ResidentEntity resident = new ResidentEntity();
    resident.setId(1L);
    PreAdmissionScreeningEntity oldPre = new PreAdmissionScreeningEntity();
    oldPre.setStatus("DRAFT");

    when(residentRepo.findById(1L)).thenReturn(Optional.of(resident));
    when(preRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.of(oldPre));

    PreCreateRequest req = new PreCreateRequest();
    req.setResidentId(1L);

    assertThrows(RuntimeException.class, () -> service.create(req));
    verify(preRepo, never()).save(argThat(p -> "DRAFT".equals(p.getStatus()) && p != oldPre));
  }

  @Test // resident exists, current record is not DRAFT (COMPLETED/REJECTED) ->
        // creates new, closes old record
  void create_existingNonDraftCurrent_closesOldAndCreatesNew() {
    ResidentEntity resident = new ResidentEntity();
    resident.setId(1L);

    PreAdmissionScreeningEntity oldPre = new PreAdmissionScreeningEntity();
    oldPre.setStatus("REJECTED");
    oldPre.setIsCurrent(true);

    when(residentRepo.findById(1L)).thenReturn(Optional.of(resident));
    when(preRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.of(oldPre));
    when(preRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PreCreateRequest req = new PreCreateRequest();
    req.setResidentId(1L);

    PreResponse res = service.create(req);
    assertFalse(oldPre.getIsCurrent());
    assertEquals("DRAFT", res.getStatus());
  }

  @Test // resident exists, no current record -> creates new successfully
  void create_noExistingCurrent_createsNewDraft() {
    ResidentEntity resident = new ResidentEntity();
    resident.setId(2L);
    resident.setFirstName("Hien");
    resident.setLastName("Nguyen");

    when(residentRepo.findById(2L)).thenReturn(Optional.of(resident));
    when(preRepo.findByResidentIdAndIsCurrentTrue(2L)).thenReturn(Optional.empty());
    when(preRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PreCreateRequest req = new PreCreateRequest();
    req.setResidentId(2L);

    PreResponse res = service.create(req);

    assertEquals("DRAFT", res.getStatus());
    assertEquals("Hien Nguyen", res.getResidentName());
  }

  @Test // status is valid = COMPLETED
  void decide_statusCompleted_success() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setResident(new ResidentEntity());
    when(preRepo.findById(1L)).thenReturn(Optional.of(pre));
    when(preRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PreDecisionRequest req = new PreDecisionRequest();
    req.setStatus("COMPLETED");

    PreResponse res = service.decide(1L, req);
    assertEquals("COMPLETED", res.getStatus());
  }

  @Test // status is valid = REJECTED
  void decide_statusRejected_success() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setResident(new ResidentEntity());
    when(preRepo.findById(1L)).thenReturn(Optional.of(pre));
    when(preRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    PreDecisionRequest req = new PreDecisionRequest();
    req.setStatus("REJECTED");

    PreResponse res = service.decide(1L, req);
    assertEquals("REJECTED", res.getStatus());
  }

  @Test // status is invalid (not in {COMPLETED, REJECTED}) -> throws exception
  void decide_invalidStatus_throwsException() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    when(preRepo.findById(1L)).thenReturn(Optional.of(pre));

    PreDecisionRequest req = new PreDecisionRequest();
    req.setStatus("PENDING");

    assertThrows(RuntimeException.class, () -> service.decide(1L, req));
  }

  @Test // id does not exist -> throws exception
  void decide_idNotFound_throwsException() {
    when(preRepo.findById(1L)).thenReturn(Optional.empty());
    PreDecisionRequest req = new PreDecisionRequest();
    req.setStatus("COMPLETED");

    assertThrows(RuntimeException.class, () -> service.decide(1L, req));
  }

  @Test // status is DRAFT -> deleted successfully
  void deleteDraft_statusDraft_deletesSuccessfully() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setStatus("DRAFT");
    when(preRepo.findById(1L)).thenReturn(Optional.of(pre));

    service.deleteDraft(1L);
    verify(preRepo).delete(pre);
  }

  @Test // status is not DRAFT (COMPLETED) -> throws exception, no deletion
  void deleteDraft_statusNotDraft_throwsException() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setStatus("COMPLETED");
    when(preRepo.findById(1L)).thenReturn(Optional.of(pre));

    assertThrows(RuntimeException.class, () -> service.deleteDraft(1L));
    verify(preRepo, never()).delete(any());
  }

  @Test // id does not exist -> throws exception
  void deleteDraft_idNotFound_throwsException() {
    when(preRepo.findById(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> service.deleteDraft(1L));
  }

  @Test // has at least 1 COMPLETED & current record
  void listCompletedForSelect_hasResults_returnsMappedList() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    ResidentEntity r = new ResidentEntity();
    r.setId(5L);
    r.setFirstName("Nguyen");
    r.setLastName("Van A");
    pre.setId(10L);
    pre.setResident(r);

    when(preRepo.findByStatusAndIsCurrentTrue("COMPLETED")).thenReturn(List.of(pre));

    List<PreSelectDTO> result = service.listCompletedForSelect();
    assertEquals(1, result.size());
    assertEquals("Nguyen Van A", result.get(0).getResidentName());
  }

  @Test // no records found -> returns empty list
  void listCompletedForSelect_noResults_returnsEmptyList() {
    when(preRepo.findByStatusAndIsCurrentTrue("COMPLETED")).thenReturn(List.of());
    assertTrue(service.listCompletedForSelect().isEmpty());
  }
}
