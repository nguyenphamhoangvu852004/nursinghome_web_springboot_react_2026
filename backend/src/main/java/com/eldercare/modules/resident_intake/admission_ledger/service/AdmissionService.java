package com.eldercare.modules.resident_intake.admission_ledger.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionCreateRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionDischargeRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionResponse;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionSelectDTO;

public interface AdmissionService {

  AdmissionResponse create(AdmissionCreateRequest request);

  Page<AdmissionResponse> listPaged(Pageable pageable);

  AdmissionResponse discharge(Long id, AdmissionDischargeRequest request);

  List<AdmissionSelectDTO> listActiveForSelect();
}