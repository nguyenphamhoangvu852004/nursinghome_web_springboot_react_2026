package com.eldercare.modules.careplan_management.careplan_design.service;

import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO.AddCareGoalResponseDTO;

import java.util.HashMap;
import java.util.List;

public interface ICareGoalService {
    AddCareGoalResponseDTO addCareGoal(AddCareGoalRequestDTO requestDTO, int carePlanId);
    HashMap<String,Object> removeCareGoal(List<Long> carePlanIds);
}
