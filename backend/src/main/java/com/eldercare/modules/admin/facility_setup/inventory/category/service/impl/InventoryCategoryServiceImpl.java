package com.eldercare.modules.admin.facility_setup.inventory.category.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.mappper.InventoryCategoryMapper;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.request.InventoryCategoryRequest;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.response.InventoryCategoryResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;
import com.eldercare.modules.admin.facility_setup.inventory.category.repository.InventoryCategoryRepository;
import com.eldercare.modules.admin.facility_setup.inventory.category.service.InventoryCategoryServiceInterface;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryCategoryServiceImpl implements InventoryCategoryServiceInterface {

    private final InventoryCategoryRepository inventoryCategoryRepository;
    private final InventoryCategoryMapper inventoryCategoryMapper;

    @Override
    public PagedResponse<List<InventoryCategoryResponse>> getInventoryCategoryList(int size, int page) {
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryCategoryEntity> categoryPage = inventoryCategoryRepository.findAll(pageable);
        List<InventoryCategoryResponse> content = categoryPage.getContent()
                .stream()
                .map(inventoryCategoryMapper::toResponse)
                .collect(Collectors.toList());
        return PagedResponse.of(content, 200, "Inventory categories retrieved successfully",
                page, categoryPage.getTotalPages(), size, categoryPage.getTotalElements());
    }

    @Transactional
    @Override
    public InventoryCategoryResponse createInventoryCategory(InventoryCategoryRequest request) {
        InventoryCategoryEntity entity = inventoryCategoryMapper.toEntity(request);
        InventoryCategoryEntity savedEntity = inventoryCategoryRepository.save(entity);
        return inventoryCategoryMapper.toResponse(savedEntity);
    }

    @Override
    public InventoryCategoryResponse getInventoryCategoryById(long id) {
        InventoryCategoryEntity entity = inventoryCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory category not found with id: " + id));
        return inventoryCategoryMapper.toResponse(entity);
    }

    @Transactional
    @Override
    public InventoryCategoryResponse updateInventoryCategory(long id, InventoryCategoryRequest request) {
        InventoryCategoryEntity entity = inventoryCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory category not found with id: " + id));
        entity.setCategoryName(request.getCategoryName());
        entity.setDescription(request.getDescription());
        InventoryCategoryEntity updatedEntity = inventoryCategoryRepository.save(entity);
        return inventoryCategoryMapper.toResponse(updatedEntity);
    }

    @Transactional
    @Override
    public void deleteInventoryCategory(long id) {
        InventoryCategoryEntity entity = inventoryCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inventory category not found with id: " + id));
        inventoryCategoryRepository.delete(entity);
    }

}
