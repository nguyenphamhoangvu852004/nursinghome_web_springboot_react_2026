package com.eldercare.modules.resident_intake.admission_ledger.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.resident_intake.admission_ledger.AdmissionEntity;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionCreateRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionDischargeRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionResponse;
import com.eldercare.modules.resident_intake.admission_ledger.repository.AdmissionRepository;
import com.eldercare.modules.resident_intake.admission_ledger.service.impl.AdmissionServiceImpl;
import com.eldercare.modules.resident_intake.pre_admission.PreAdmissionScreeningEntity;
import com.eldercare.modules.resident_intake.pre_admission.repository.PreAdmissionScreeningRepository;
import com.eldercare.modules.resident_intake.resident.repository.ResidentRepository;
import com.eldercare.modules.resident_intake.resident_profile.ResidentEntity;

@ExtendWith(MockitoExtension.class)
public class AdmissionServiceTests {
  @Mock
  private AdmissionRepository admRepo;
  @Mock
  private PreAdmissionScreeningRepository preRepo;
  @Mock
  private ResidentRepository residentRepo;
  @InjectMocks
  private AdmissionServiceImpl service;

  @Test // pre-admission does not exist -> throws exception
  void create_preNotFound_throwsException() {
    when(preRepo.findById(1L)).thenReturn(Optional.empty());
    AdmissionCreateRequest req = new AdmissionCreateRequest();
    req.setPreAdmissionScreeningId(1L);

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // pre-admission status is not COMPLETED -> throws exception
  void create_preNotCompleted_throwsException() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setStatus("DRAFT");
    pre.setIsCurrent(true);
    when(preRepo.findById(1L)).thenReturn(Optional.of(pre));

    AdmissionCreateRequest req = new AdmissionCreateRequest();
    req.setPreAdmissionScreeningId(1L);

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // pre-admission is COMPLETED but isCurrent is false -> throws exception
  void create_preNotCurrent_throwsException() {
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setStatus("COMPLETED");
    pre.setIsCurrent(false);
    when(preRepo.findById(1L)).thenReturn(Optional.of(pre));

    AdmissionCreateRequest req = new AdmissionCreateRequest();
    req.setPreAdmissionScreeningId(1L);

    assertThrows(RuntimeException.class, () -> service.create(req));
  }

  @Test // valid pre-admission (COMPLETED + current), resident has no current admission
        // -> created successfully
  void create_validPre_noExistingAdmission_createsSuccessfully() {
    ResidentEntity resident = new ResidentEntity();
    resident.setId(1L);
    resident.setFirstName("A");
    resident.setLastName("B");
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setId(5L);
    pre.setStatus("COMPLETED");
    pre.setIsCurrent(true);
    pre.setResident(resident);

    when(preRepo.findById(5L)).thenReturn(Optional.of(pre));
    when(admRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.empty());
    when(admRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(residentRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    AdmissionCreateRequest req = new AdmissionCreateRequest();
    req.setPreAdmissionScreeningId(5L);
    req.setFacilityId(2L);
    req.setAdmissionDate(LocalDate.now());

    AdmissionResponse res = service.create(req);

    assertEquals("ACTIVE", resident.getStatus());
    assertEquals("ACTIVE", res.getStatus());
  }

  @Test // resident already has current admission -> old admission is closed
        // (isCurrent=false)
  void create_existingCurrentAdmission_closesOldOne() {
    ResidentEntity resident = new ResidentEntity();
    resident.setId(1L);
    PreAdmissionScreeningEntity pre = new PreAdmissionScreeningEntity();
    pre.setId(5L);
    pre.setStatus("COMPLETED");
    pre.setIsCurrent(true);
    pre.setResident(resident);

    AdmissionEntity oldAdm = new AdmissionEntity();
    oldAdm.setIsCurrent(true);

    when(preRepo.findById(5L)).thenReturn(Optional.of(pre));
    when(admRepo.findByResidentIdAndIsCurrentTrue(1L)).thenReturn(Optional.of(oldAdm));
    when(admRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(residentRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    AdmissionCreateRequest req = new AdmissionCreateRequest();
    req.setPreAdmissionScreeningId(5L);
    req.setFacilityId(2L);
    req.setAdmissionDate(LocalDate.now());

    service.create(req);

    assertFalse(oldAdm.getIsCurrent());
  }

  @Test // id does not exist -> throws exception
  void discharge_idNotFound_throwsException() {
    when(admRepo.findById(1L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class,
        () -> service.discharge(1L, new AdmissionDischargeRequest()));
  }

  @Test // valid discharge -> updates admission + sets resident status to DISCHARGED
  void discharge_validAdmission_updatesStatuses() {
    ResidentEntity resident = new ResidentEntity();
    AdmissionEntity adm = new AdmissionEntity();
    adm.setId(1L);
    adm.setResident(resident);
    adm.setFacility(new FacilityEntity());
    adm.setPreAdmissionScreening(new PreAdmissionScreeningEntity());

    when(admRepo.findById(1L)).thenReturn(Optional.of(adm));
    when(admRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    when(residentRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

    AdmissionDischargeRequest req = new AdmissionDischargeRequest();
    req.setDischargeDate(LocalDate.now());
    req.setDischargeReason("Family request");

    AdmissionResponse res = service.discharge(1L, req);

    assertEquals("DISCHARGED", resident.getStatus());
    assertEquals("DISCHARGED", res.getStatus());
    assertFalse(adm.getIsCurrent());
  }

  @Test // active admissions exist -> returns list
  void listActiveForSelect_hasActive_returnsList() {
    AdmissionEntity adm = new AdmissionEntity();
    adm.setId(1L);
    ResidentEntity r = new ResidentEntity();
    r.setFirstName("X");
    r.setLastName("Y");
    adm.setResident(r);

    when(admRepo.findByIsCurrentTrueAndDischargeDateIsNull()).thenReturn(List.of(adm));

    assertEquals(1, service.listActiveForSelect().size());
  }

  @Test // no active admissions -> returns empty list
  void listActiveForSelect_noActive_returnsEmpty() {
    when(admRepo.findByIsCurrentTrueAndDischargeDateIsNull()).thenReturn(List.of());
    assertTrue(service.listActiveForSelect().isEmpty());
  }
}
