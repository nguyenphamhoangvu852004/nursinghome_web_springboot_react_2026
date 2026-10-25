package com.eldercare.modules.careplan_management.careplan_design.dto.addCareGoalDTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddCareGoalRequestDTO {
    public String careGoalName;
    public String careGoalDescription;
}
