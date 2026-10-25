package com.eldercare.modules.careplan_management.careplan_design.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.service.ICareGoalService;

@RestController
@RequestMapping("/api/v2/legacy/care-plans/{carePlanId}/goals")
public class CareGoalController {
    private final ICareGoalService careGoalService;

    public CareGoalController(ICareGoalService careGoalService) {
        this.careGoalService = careGoalService;
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<AddCareGoalResponseDTO>> createGoal(@PathVariable() int carePlanId,
            @RequestBody() AddCareGoalRequestDTO request) {
        AddCareGoalRequestDTO requestDTO = new AddCareGoalRequestDTO(
                request.careGoalName,
                request.careGoalDescription);
        AddCareGoalResponseDTO responseDTO = this.careGoalService.addCareGoal(requestDTO,
                carePlanId);
        ApiResponse<AddCareGoalResponseDTO> res = ApiResponse
                .created("Care goal id " + responseDTO.careGoalId + " created.", responseDTO);
        return ResponseEntity.ok(res);
    }

}
