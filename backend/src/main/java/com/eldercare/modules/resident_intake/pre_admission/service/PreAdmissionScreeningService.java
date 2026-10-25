package com.eldercare.modules.resident_intake.pre_admission.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreCreateRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreDecisionRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreResponse;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreSelectDTO;

public interface PreAdmissionScreeningService {

    PreResponse create(PreCreateRequest request);

    Page<PreResponse> listPaged(Pageable pageable);

    List<PreSelectDTO> listCompletedForSelect();

    PreResponse decide(Long id, PreDecisionRequest request);

    void deleteDraft(Long id);
}