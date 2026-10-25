package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyUpdateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.response.ConsumableSupplyResponse;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.service.ConsumableSupplyService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES)
@RequiredArgsConstructor
public class ConsumableSupplyController {

    private final ConsumableSupplyService consumableSupplyService;

    @GetMapping
    public ResponseEntity<PagedResponse<List<ConsumableSupplyResponse>>> getAllConsumableSupplies(
            @PositiveOrZero(message = "Page must be a positive number") @RequestParam(defaultValue = "0") int page,
            @Positive(message = "Size must be a positive number") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(consumableSupplyService.getAllConsumableSupplies(page, size));
    }

    @PostMapping
    public ResponseEntity<ConsumableSupplyResponse> createConsumableSupply(
            @Valid @RequestBody ConsumableSupplyCreateRequest consumableSupplyRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consumableSupplyService.createConsumableSupply(consumableSupplyRequest));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<PagedResponse<List<ConsumableSupplyResponse>>> getReorderSupplies(
            @PositiveOrZero(message = "Page must be a positive number") @RequestParam(defaultValue = "0") int page,
            @Positive(message = "Size must be a positive number") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(consumableSupplyService.getLowStockSupplies(page, size));
    }

    @GetMapping("/{supplyId}")
    public ResponseEntity<ConsumableSupplyResponse> getConsumableSupplyDetails(
            @PathVariable Long supplyId) {
        return ResponseEntity.ok(consumableSupplyService.getConsumableSupplyById(supplyId));
    }

    @PutMapping("/{supplyId}")
    public ResponseEntity<ConsumableSupplyResponse> updateConsumableSupply(
            @PathVariable Long supplyId,
            @Valid @RequestBody ConsumableSupplyUpdateRequest consumableSupplyRequest) {
        return ResponseEntity.ok(consumableSupplyService.updateConsumableSupply(supplyId, consumableSupplyRequest));
    }

    @DeleteMapping("/{supplyId}")
    public ResponseEntity<Void> deleteConsumableSupply(@PathVariable Long supplyId) {
        consumableSupplyService.deleteConsumableSupply(supplyId);
        return ResponseEntity.noContent().build();
    }
}
