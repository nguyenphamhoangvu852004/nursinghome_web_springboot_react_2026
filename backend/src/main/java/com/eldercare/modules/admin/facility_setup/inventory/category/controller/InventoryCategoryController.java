package com.eldercare.modules.admin.facility_setup.inventory.category.controller;

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
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.request.InventoryCategoryRequest;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.response.InventoryCategoryResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.service.InventoryCategoryServiceInterface;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(RouteConstants.API_ADMIN_INVENTORY_CATEGORIES)
@RequiredArgsConstructor
public class InventoryCategoryController {

    private final InventoryCategoryServiceInterface inventoryCategoryService;

    @GetMapping
    public ResponseEntity<PagedResponse<List<InventoryCategoryResponse>>> getAllInventoryCategories(
            @PositiveOrZero(message = "Page must be zero or a positive number") @RequestParam(defaultValue = "0") int page,
            @Positive(message = "Size must be a positive number") @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(inventoryCategoryService.getInventoryCategoryList(size, page));
    }

    @PostMapping
    public ResponseEntity<InventoryCategoryResponse> createInventoryCategory(
            @Valid @RequestBody InventoryCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventoryCategoryService.createInventoryCategory(request));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<InventoryCategoryResponse> getInventoryCategoryById(
            @Positive(message = "Category ID must be a positive number") @PathVariable Long categoryId) {
        return ResponseEntity.ok(inventoryCategoryService.getInventoryCategoryById(categoryId));
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<InventoryCategoryResponse> updateInventoryCategory(
            @Positive(message = "Category ID must be a positive number") @PathVariable Long categoryId,
            @Valid @RequestBody InventoryCategoryRequest request) {
        return ResponseEntity.ok(inventoryCategoryService.updateInventoryCategory(categoryId, request));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteInventoryCategory(@PathVariable Long categoryId) {
        inventoryCategoryService.deleteInventoryCategory(categoryId);
        return ResponseEntity.noContent().build();
    }

}
