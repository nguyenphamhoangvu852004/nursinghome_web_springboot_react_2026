package com.eldercare.modules.careplan_management.careplan_design.service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.searchCarePlanDTO.SearchCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.searchCarePlanDTO.SearchCarePlanResponseDTO;

import java.util.List;

public interface ICarePlanService {
    public ActiveCarePlanResponseDTO activateCarePlan(ActiveCarePlanRequestDTO requestDTO);

    public DiscontinueCarePlanResponseDTO discontinueCarePlan(DiscontinueCarePlanRequestDTO requestDTO);

    public MarkSignificantChangeResponseDTO markSignificantChange(MarkSignificantChangeRequestDTO requestDTO);

    public PagedResponse<ListCarePlanResponseDTO> listCarePlans(ListCarePlanRequestDTO requestDTO);

    public GetCarePlanDetailResponseDTO getCarePlanDetail(GetCarePlanDetailRequestDTO requestDTO);

    public PagedResponse<ListCarePlanResponseDTO> searchCarePlan(SearchCarePlanRequestDTO requestDTO);

    public CreateCarePlanResponseDTO createCarePlan(CreateCarePlanRequestDTO requestDTO, String purpose);

    public DeleteCarePlanResponseDTO softDeleteCarePlan(DeleteCarePlanRequestDTO requestDTO);
}
