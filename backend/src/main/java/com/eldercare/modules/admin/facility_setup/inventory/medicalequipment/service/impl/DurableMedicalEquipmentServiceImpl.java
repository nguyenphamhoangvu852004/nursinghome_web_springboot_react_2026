package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.service.impl;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;
import com.eldercare.modules.admin.facility_setup.inventory.category.repository.InventoryCategoryRepository;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.mapper.DurableMedicalEquipmentMapper;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentChangeStatusRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.DurableMedicalEquipmentUpdateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.response.DurableMedicalEquipmentResponse;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.entity.DurableMedicalEquipmentEntity;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.enums.DurableMedicalEquipmentEnum;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.repository.DurableMedicalEquipmentRepository;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.service.DurableMedicalEquipmentServiceInterface;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DurableMedicalEquipmentServiceImpl implements DurableMedicalEquipmentServiceInterface {

    private final DurableMedicalEquipmentRepository durableMedicalEquipmentRepository;

    private final DurableMedicalEquipmentMapper durableMedicalEquipmentMapper;

    private final InventoryCategoryRepository inventoryCategoryRepository;

    private final FacilityRepository facilityRepository;

    private final UserRepository userRepository;

    @Override
    public PagedResponse<List<DurableMedicalEquipmentResponse>> getAllDurableMedicalEquipment(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DurableMedicalEquipmentEntity> equipmentPage = durableMedicalEquipmentRepository
                .findAll(pageable);
        List<DurableMedicalEquipmentResponse> content = equipmentPage.getContent()
                .stream()
                .map(durableMedicalEquipmentMapper::toResponse)
                .toList();
        System.out.println(content);
        return PagedResponse.of(content, 200, "Durable medical equipment retrieved successfully",
                page, equipmentPage.getTotalPages(), size, equipmentPage.getTotalElements());
    }

    @Override
    @Transactional
    public DurableMedicalEquipmentResponse createEquipment(
            DurableMedicalEquipmentCreateRequest durableMedicalEquipmentRequest) {
        if (durableMedicalEquipmentRepository
                .findByAssetTagAndIsDeletedFalse(durableMedicalEquipmentRequest.getAssetTag()).isPresent()) {
            throw new RuntimeException("Asset tag already exists");
        }
        long newCategoryId = durableMedicalEquipmentRequest.getCategoryId();
        InventoryCategoryEntity newCategory = inventoryCategoryRepository.getReferenceById(newCategoryId);
        long newFacilityId = durableMedicalEquipmentRequest.getFacilityId();
        FacilityEntity newFacility = facilityRepository.getReferenceById(newFacilityId);
        DurableMedicalEquipmentEntity entity = durableMedicalEquipmentMapper.toEntity(durableMedicalEquipmentRequest);
        entity.setCategory(newCategory);
        entity.setFacility(newFacility);
        DurableMedicalEquipmentEntity savedEntity = durableMedicalEquipmentRepository.save(entity);
        return durableMedicalEquipmentMapper.toResponse(savedEntity);
    }

    @Override
    public DurableMedicalEquipmentResponse getEquipmentById(Long id) {
        DurableMedicalEquipmentEntity entity = durableMedicalEquipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Durable medical equipment not found with id: " + id));
        return durableMedicalEquipmentMapper.toResponse(entity);
    }

    @Override
    @Transactional
    public DurableMedicalEquipmentResponse updateEquipment(Long id,
            DurableMedicalEquipmentUpdateRequest durableMedicalEquipmentRequest) {
        DurableMedicalEquipmentEntity entity = durableMedicalEquipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Durable medical equipment not found with id: " + id));
        long newCategoryId = durableMedicalEquipmentRequest.getCategoryId();
        InventoryCategoryEntity newCategory = inventoryCategoryRepository.getReferenceById(newCategoryId);
        long newFacilityId = durableMedicalEquipmentRequest.getFacilityId();
        FacilityEntity newFacility = facilityRepository.getReferenceById(newFacilityId);

        entity.setItemName(durableMedicalEquipmentRequest.getItemName());
        entity.setCategory(newCategory);
        entity.setFacility(newFacility);
        entity.setUnitValue(durableMedicalEquipmentRequest.getUnitValue());
        DurableMedicalEquipmentEntity updatedEntity = durableMedicalEquipmentRepository.save(entity);
        return durableMedicalEquipmentMapper.toResponse(updatedEntity);
    }

    @Override
    @Transactional
    public void deleteEquipment(Long id) {
        if (durableMedicalEquipmentRepository
                .findByIdAndStatusAndIsDeletedFalse(id, DurableMedicalEquipmentEnum.RETIRED.toString())
                .isPresent()) {
            DurableMedicalEquipmentEntity entityToDelete = durableMedicalEquipmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Durable medical equipment not found with id: " + id));
            entityToDelete.setIsDeleted(true);
            durableMedicalEquipmentRepository.save(entityToDelete);
        } else {
            throw new RuntimeException("Durable medical equipment cannot be deleted unless it is retired");
        }
    }

    @Override
    @Transactional
    public DurableMedicalEquipmentResponse patchEquipmentStatus(long id,
            DurableMedicalEquipmentChangeStatusRequest durableMedicalEquipmentRequest) {

        if (durableMedicalEquipmentRepository
                .findByIdAndStatusAndIsDeletedFalse(id, DurableMedicalEquipmentEnum.RETIRED.toString()).isPresent()) {
            throw new RuntimeException("Durable medical equipment cannot be updated as it is retired");
        }

        DurableMedicalEquipmentEntity entity = durableMedicalEquipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Durable medical equipment not found with id: " + id));
        DurableMedicalEquipmentEnum newStatus = DurableMedicalEquipmentEnum
                .valueOf(durableMedicalEquipmentRequest.getStatus().toUpperCase().trim());
        entity.setStatus(newStatus);
        DurableMedicalEquipmentEntity updatedEntity = durableMedicalEquipmentRepository.save(entity);
        return durableMedicalEquipmentMapper.toResponse(updatedEntity);
    }

//     @Override
//     public DurableMedicalEquipmentResponse assignEquipmentForUser(Long id,
//             DurableMedicalEquipmentCreateRequest durableMedicalEquipmentRequest) {
//         if (durableMedicalEquipmentRepository
//                 .findByIdAndStatusAndIsDeletedFalse(id, DurableMedicalEquipmentEnum.AVAILABLE.toString()).isPresent()) {
//             DurableMedicalEquipmentEntity entity = durableMedicalEquipmentRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("Durable medical equipment not found with id: " + id));
//             long newAssignedToUserId = durableMedicalEquipmentRequest.getAssignedToUserId();
//             UserEntity newAssignedToUser = userRepository.getReferenceById(newAssignedToUserId);
//             entity.setAssignedToUser(newAssignedToUser);
//             entity.setStatus(DurableMedicalEquipmentEnum.IN_SERVICE);
//             DurableMedicalEquipmentEntity updatedEntity = durableMedicalEquipmentRepository.save(entity);
//             return durableMedicalEquipmentMapper.toResponse(updatedEntity);
//         }
//         throw new RuntimeException(
//                 "Durable medical equipment cannot be assigned unless it is available");
//     }

//     @Override
//     public DurableMedicalEquipmentResponse unassignEquipmentForUser(Long id) {
//         if (durableMedicalEquipmentRepository
//                 .findByIdAndStatusAndIsDeletedFalse(id, DurableMedicalEquipmentEnum.IN_SERVICE.toString())
//                 .isPresent()) {
//             DurableMedicalEquipmentEntity entity = durableMedicalEquipmentRepository.findById(id)
//                     .orElseThrow(() -> new RuntimeException("Durable medical equipment not found with id: " + id));
//             entity.setAssignedToUser(null);
//             entity.setStatus(DurableMedicalEquipmentEnum.AVAILABLE);
//             DurableMedicalEquipmentEntity updatedEntity = durableMedicalEquipmentRepository.save(entity);
//             return durableMedicalEquipmentMapper.toResponse(updatedEntity);
//         }
//         throw new RuntimeException(
//                 "Durable medical equipment cannot be unassigned unless it is in service");
//     }

}
