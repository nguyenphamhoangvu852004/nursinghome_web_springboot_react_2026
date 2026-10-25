package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentChangeStatusRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentUpdateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.response.DurableMedicalEquipmentResponse;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.service.DurableMedicalEquipmentServiceInterface;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT)
@RequiredArgsConstructor
public class DurableMedicalEquipmentController {

    private final DurableMedicalEquipmentServiceInterface durableMedicalEquipmentService;

    @GetMapping
    public ResponseEntity<PagedResponse<List<DurableMedicalEquipmentResponse>>> getEquipmentList(
            @PositiveOrZero(message = "Page must be zero or a positive number") @RequestParam(defaultValue = "0") int page,
            @Positive(message = "Size must be a positive number") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(durableMedicalEquipmentService.getAllDurableMedicalEquipment(page, size));
    }

    @PostMapping
    public ResponseEntity<DurableMedicalEquipmentResponse> createEquipment(
            @Valid @RequestBody DurableMedicalEquipmentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(durableMedicalEquipmentService.createEquipment(request));
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<DurableMedicalEquipmentResponse> getEquipmentDetails(
            @PathVariable Long equipmentId) {
        return ResponseEntity.ok(durableMedicalEquipmentService.getEquipmentById(equipmentId));
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<DurableMedicalEquipmentResponse> updateEquipment(
            @PathVariable Long equipmentId,
            @Valid @RequestBody DurableMedicalEquipmentUpdateRequest request) {
        return ResponseEntity.ok(durableMedicalEquipmentService.updateEquipment(equipmentId, request));
    }

    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable Long equipmentId) {
        durableMedicalEquipmentService.deleteEquipment(equipmentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{equipmentId}/status")
    public ResponseEntity<DurableMedicalEquipmentResponse> updateEquipmentStatus(
            @PathVariable Long equipmentId,
            @Valid @RequestBody DurableMedicalEquipmentChangeStatusRequest request) {
        return ResponseEntity.ok(durableMedicalEquipmentService.patchEquipmentStatus(equipmentId, request));
    }

    // @PatchMapping("/{equipmentId}/assign")
    // public ResponseEntity<DurableMedicalEquipmentResponse> assignEquipment(
    // @Positive(message = "Equipment ID must be a positive number") @RequestParam
    // Long equipmentId,
    // @Valid @RequestBody DurableMedicalEquipmentCreateRequest request) {
    // return
    // ResponseEntity.ok(durableMedicalEquipmentService.assignEquipmentForUser(equipmentId,
    // request));
    // }

    // @PatchMapping("/{equipmentId}/unassign")
    // public ResponseEntity<DurableMedicalEquipmentResponse> unassignEquipment(
    // @Positive(message = "Equipment ID must be a positive number") @RequestParam
    // Long equipmentId) {
    // return
    // ResponseEntity.ok(durableMedicalEquipmentService.unassignEquipmentForUser(equipmentId));
    // }

}
