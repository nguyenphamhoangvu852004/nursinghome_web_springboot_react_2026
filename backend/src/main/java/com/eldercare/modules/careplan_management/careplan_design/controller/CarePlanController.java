package com.eldercare.modules.careplan_management.careplan_design.controller;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.createCarePlanDTO.CreateCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.deleteCarePlanDTO.DeleteCarePlanResponseDTO;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.eldercare.common.enums.CarePlanStatusEnum;
import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.activeCarePlanDTO.ActiveCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.discontinueCarePlanDTO.DiscontinueCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.getCarePlanDetailDTO.GetCarePlanDetailResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.listCarePlansDTO.ListCarePlanResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeResponseDTO;
import com.eldercare.modules.careplan_management.careplan_design.dto.markSignificantChangeDTO.MarkSignificantChangeRequestDTO;
import com.eldercare.modules.careplan_management.careplan_design.service.ICarePlanService;

@RestController
@RequestMapping("/api/v2/legacy/care-plans")
public class CarePlanController {
    private final ICarePlanService carePlanService;

    public CarePlanController(ICarePlanService carePlanService) {
        this.carePlanService = carePlanService;
    }

    @PatchMapping("/{carePlanId}/activate")
    public ResponseEntity<ApiResponse<ActiveCarePlanResponseDTO>> activateCarePlan(@PathVariable int carePlanId) {
        ActiveCarePlanResponseDTO data = this.carePlanService.activateCarePlan(
                new ActiveCarePlanRequestDTO(carePlanId));
        ApiResponse<ActiveCarePlanResponseDTO> response = ApiResponse.success(data);
        response.setMessage("Care plan " + carePlanId + " activated successfully");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{carePlanId}/discontinue")
    public ResponseEntity<ApiResponse<DiscontinueCarePlanResponseDTO>> discontinueCarePlan(
            @PathVariable int carePlanId) {
        ApiResponse<DiscontinueCarePlanResponseDTO> response = ApiResponse
                .success(this.carePlanService.discontinueCarePlan(new DiscontinueCarePlanRequestDTO(carePlanId)));
        response.setMessage("Care plan " + carePlanId + " deactivated");
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{carePlanId}/significant-change")
    public ResponseEntity<ApiResponse<MarkSignificantChangeResponseDTO>> markSignificantChange(
            @PathVariable int carePlanId) {
        ApiResponse<MarkSignificantChangeResponseDTO> response = ApiResponse
                .success(this.carePlanService.markSignificantChange(new MarkSignificantChangeRequestDTO(carePlanId)));
        response.setMessage("Significant change flag updated");
        return ResponseEntity.ok(response);
    }

    @GetMapping("")
    public ResponseEntity<PagedResponse<ListCarePlanResponseDTO>> listCarePlans(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long residentId,
            @RequestParam(required = false) String residentName,
            @RequestParam(required = false) CarePlanStatusEnum status,
            @RequestParam(required = false) Boolean significantChangeFlag,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {

        ListCarePlanRequestDTO request = new ListCarePlanRequestDTO(
                page, size, residentId, residentName, status,
                significantChangeFlag, sortBy, sortDir);
        PagedResponse<ListCarePlanResponseDTO> paginatedResponse = carePlanService.listCarePlans(request);
        return ResponseEntity.ok(paginatedResponse);
    }

    @GetMapping("/{carePlanId}")
    public ResponseEntity<ApiResponse<GetCarePlanDetailResponseDTO>> getCarePlanDetail(@PathVariable int carePlanId) {
        GetCarePlanDetailRequestDTO requestDTO = new GetCarePlanDetailRequestDTO();
        requestDTO.id = carePlanId;

        ApiResponse<GetCarePlanDetailResponseDTO> response = ApiResponse
                .success(this.carePlanService.getCarePlanDetail(requestDTO));
        return ResponseEntity.ok(response);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<CreateCarePlanResponseDTO>> createCarePlan(
            @RequestBody() CreateCarePlanRequestDTO requestDTO,
            @RequestParam(defaultValue = "DRAFT") String purpose) {

        ApiResponse<CreateCarePlanResponseDTO> response = ApiResponse
                .success(this.carePlanService.createCarePlan(requestDTO, purpose));
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{carePlanId}")
    public ResponseEntity<ApiResponse<DeleteCarePlanResponseDTO>> deleteCarePlan(
            @PathVariable() int carePlanId) {
        DeleteCarePlanRequestDTO requetsDTO = new DeleteCarePlanRequestDTO(carePlanId);
        DeleteCarePlanResponseDTO responseDTO = this.carePlanService.softDeleteCarePlan(requetsDTO);
        ApiResponse<DeleteCarePlanResponseDTO> response = ApiResponse.success(responseDTO);
        return ResponseEntity.ok(response);
    }
}
