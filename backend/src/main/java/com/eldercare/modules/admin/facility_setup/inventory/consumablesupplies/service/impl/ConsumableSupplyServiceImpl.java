package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;
import com.eldercare.modules.admin.facility_setup.inventory.category.repository.InventoryCategoryRepository;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.mapper.ConsumableSupplyMapper;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyUpdateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.response.ConsumableSupplyResponse;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.entity.ConsumableSupplyEntity;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.enums.ConsumableSupplyEnum;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.repository.ConsumableSupplyRepository;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.service.ConsumableSupplyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConsumableSupplyServiceImpl implements ConsumableSupplyService {

    private final ConsumableSupplyRepository consumableSupplyRepository;

    private final ConsumableSupplyMapper consumableSupplyMapper;

    private final InventoryCategoryRepository inventoryCategoryRepository;

    private final FacilityRepository facilityRepository;

    @Override
    public PagedResponse<List<ConsumableSupplyResponse>> getAllConsumableSupplies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsumableSupplyEntity> consumableSupplyPage = consumableSupplyRepository
                .findAll(pageable);

        List<ConsumableSupplyResponse> content = consumableSupplyPage.getContent()
                .stream()
                .map(consumableSupplyMapper::toResponse)
                .toList();
        System.out.println("Content: " + content);
        return PagedResponse.of(content, 200, "Consumable supplies retrieved successfully",
                page, consumableSupplyPage.getTotalPages(), size, consumableSupplyPage.getTotalElements());
    }

    @Override
    @Transactional
    public ConsumableSupplyResponse createConsumableSupply(ConsumableSupplyCreateRequest consumableSupplyRequest) {
        ConsumableSupplyEntity consumableSupplyEntity = consumableSupplyMapper.toEntity(consumableSupplyRequest);
        long newCategoryId = consumableSupplyRequest.getCategoryId();
        InventoryCategoryEntity newCategory = inventoryCategoryRepository.getReferenceById(newCategoryId);
        long newFacilityId = consumableSupplyRequest.getFacilityId();
        FacilityEntity newFacility = facilityRepository.getReferenceById(newFacilityId);
        consumableSupplyEntity.setCategory(newCategory);
        consumableSupplyEntity.setFacility(newFacility);
        consumableSupplyEntity.setStatus(getSupplyStatus(consumableSupplyRequest));
        consumableSupplyEntity.setTotal(consumableSupplyRequest.getStockOnHand());
        ConsumableSupplyEntity savedEntity = consumableSupplyRepository.save(consumableSupplyEntity);
        return consumableSupplyMapper.toResponse(savedEntity);
    }

    @Override
    public PagedResponse<List<ConsumableSupplyResponse>> getLowStockSupplies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsumableSupplyEntity> consumableSupplyPage = consumableSupplyRepository
                .findByStatusAndIsDeletedFalse(ConsumableSupplyEnum.LOW_STOCK.toString(), pageable);

        List<ConsumableSupplyResponse> content = consumableSupplyPage.getContent()
                .stream()
                .map(consumableSupplyMapper::toResponse)
                .toList();

        return PagedResponse.of(content, 200, "Active consumable supplies retrieved successfully",
                page, consumableSupplyPage.getTotalPages(), size, consumableSupplyPage.getTotalElements());
    }

    @Override
    public ConsumableSupplyResponse getConsumableSupplyById(Long id) {
        ConsumableSupplyEntity consumableSupplyEntity = consumableSupplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumable supply not found with id: " + id));
        return consumableSupplyMapper.toResponse(consumableSupplyEntity);
    }

    @Override
    @Transactional
    public ConsumableSupplyResponse updateConsumableSupply(Long id,
            ConsumableSupplyUpdateRequest consumableSupplyRequest) {
        ConsumableSupplyEntity consumableSupplyEntity = consumableSupplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumable supply not found with id: " + id));
        long newFacilityId = consumableSupplyRequest.getFacilityId();
        long newCategoryId = consumableSupplyRequest.getCategoryId();
        InventoryCategoryEntity newCategory = inventoryCategoryRepository.getReferenceById(newCategoryId);
        FacilityEntity newFacility = facilityRepository.getReferenceById(newFacilityId);
        consumableSupplyEntity.setItemName(consumableSupplyRequest.getItemName());
        consumableSupplyEntity.setCategory(newCategory);
        consumableSupplyEntity.setFacility(newFacility);
        consumableSupplyEntity.setReorderThreshold(consumableSupplyRequest.getReorderThreshold());
        consumableSupplyEntity.setUnitCost(consumableSupplyRequest.getUnitCost());
        consumableSupplyEntity.setPrivatePayRate(consumableSupplyRequest.getPrivatePayRate());
        ConsumableSupplyEntity updatedEntity = consumableSupplyRepository.save(consumableSupplyEntity);
        return consumableSupplyMapper.toResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteConsumableSupply(Long id) {
        ConsumableSupplyEntity consumableSupplyEntity = consumableSupplyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consumable supply not found with id: " + id));
        consumableSupplyEntity.setIsDeleted(true);
        consumableSupplyRepository.save(consumableSupplyEntity);
    }

    private ConsumableSupplyEnum getSupplyStatus(ConsumableSupplyCreateRequest consumableSupplyRequest) {

        if (consumableSupplyRequest.getStockOnHand() <= 0) {
            return ConsumableSupplyEnum.OUT_OF_STOCK;
        } else if (consumableSupplyRequest.getStockOnHand() <= consumableSupplyRequest.getReorderThreshold()) {
            return ConsumableSupplyEnum.LOW_STOCK;
        } else {
            return ConsumableSupplyEnum.OK;
        }
    }
}
