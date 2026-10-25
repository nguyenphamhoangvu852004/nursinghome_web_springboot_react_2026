package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.entity.FacilityEntity;
import com.eldercare.modules.admin.facility_setup.facility.facility_profile.repository.FacilityRepository;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;
import com.eldercare.modules.admin.facility_setup.inventory.category.repository.InventoryCategoryRepository;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.mapper.DurableMedicalEquipmentMapper;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.*;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.response.DurableMedicalEquipmentResponse;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.entity.DurableMedicalEquipmentEntity;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.enums.DurableMedicalEquipmentEnum;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.repository.DurableMedicalEquipmentRepository;
import com.eldercare.modules.admin.user_management.UserRepository;

@ExtendWith(MockitoExtension.class)
class DurableMedicalEquipmentServiceImplTest {

    @Mock private DurableMedicalEquipmentRepository repository;
    @Mock private DurableMedicalEquipmentMapper mapper;
    @Mock private InventoryCategoryRepository categoryRepository;
    @Mock private FacilityRepository facilityRepository;
    @Mock private UserRepository userRepository; // Được khai báo trong service

    @InjectMocks
    private DurableMedicalEquipmentServiceImpl service;

    private DurableMedicalEquipmentEntity mockEntity;
    private final Long ID = 1L;
    private final String ASSET_TAG = "TAG-001";

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
        mockEntity = DurableMedicalEquipmentEntity.builder()
                .id(ID)
                .assetTag(ASSET_TAG)
                .status(DurableMedicalEquipmentEnum.AVAILABLE)
                .isDeleted(false)
                .build();
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    @Test
    void getAllDurableMedicalEquipment_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DurableMedicalEquipmentEntity> mockPage = new PageImpl<>(List.of(mockEntity), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(mockPage);
        when(mapper.toResponse(mockEntity)).thenReturn(new DurableMedicalEquipmentResponse());

        PagedResponse<List<DurableMedicalEquipmentResponse>> result = service.getAllDurableMedicalEquipment(0, 10);

        assertNotNull(result);
    }

    @Test
    void createEquipment_Success() {
        DurableMedicalEquipmentCreateRequest request = new DurableMedicalEquipmentCreateRequest();
        request.setAssetTag(ASSET_TAG);
        request.setCategoryId(1L);
        request.setFacilityId(1L);

        when(repository.findByAssetTagAndIsDeletedFalse(ASSET_TAG)).thenReturn(Optional.empty());
        when(categoryRepository.getReferenceById(1L)).thenReturn(new InventoryCategoryEntity());
        when(facilityRepository.getReferenceById(1L)).thenReturn(new FacilityEntity());
        when(mapper.toEntity(request)).thenReturn(mockEntity);
        when(repository.save(any())).thenReturn(mockEntity);

        service.createEquipment(request);

        verify(repository).save(any());
    }

    @Test
    void createEquipment_Fails_WhenAssetTagExists() {
        DurableMedicalEquipmentCreateRequest request = new DurableMedicalEquipmentCreateRequest();
        request.setAssetTag(ASSET_TAG);
        when(repository.findByAssetTagAndIsDeletedFalse(ASSET_TAG)).thenReturn(Optional.of(mockEntity));

        assertThrows(RuntimeException.class, () -> service.createEquipment(request));
    }

    @Test
    void getEquipmentById_Success() {
        when(repository.findById(ID)).thenReturn(Optional.of(mockEntity));
        when(mapper.toResponse(mockEntity)).thenReturn(new DurableMedicalEquipmentResponse());

        service.getEquipmentById(ID);
        verify(repository).findById(ID);
    }

    @Test
    void updateEquipment_Success() {
        DurableMedicalEquipmentUpdateRequest request = new DurableMedicalEquipmentUpdateRequest();
        request.setCategoryId(1L);
        request.setFacilityId(1L);

        when(repository.findById(ID)).thenReturn(Optional.of(mockEntity));
        when(categoryRepository.getReferenceById(1L)).thenReturn(new InventoryCategoryEntity());
        when(facilityRepository.getReferenceById(1L)).thenReturn(new FacilityEntity());
        when(repository.save(any())).thenReturn(mockEntity);

        service.updateEquipment(ID, request);

        verify(repository).save(mockEntity);
    }

    @Test
    void deleteEquipment_Success_WhenRetired() {
        when(repository.findByIdAndStatusAndIsDeletedFalse(ID, DurableMedicalEquipmentEnum.RETIRED.toString()))
                .thenReturn(Optional.of(mockEntity));
        when(repository.findById(ID)).thenReturn(Optional.of(mockEntity));

        service.deleteEquipment(ID);

        assertTrue(mockEntity.getIsDeleted());
        verify(repository).save(mockEntity);
    }

    @Test
    void deleteEquipment_Fails_WhenNotRetired() {
        when(repository.findByIdAndStatusAndIsDeletedFalse(ID, DurableMedicalEquipmentEnum.RETIRED.toString()))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.deleteEquipment(ID));
    }

    @Test
    void patchEquipmentStatus_Success() {
        DurableMedicalEquipmentChangeStatusRequest request = new DurableMedicalEquipmentChangeStatusRequest();
        request.setStatus("IN_SERVICE");

        when(repository.findByIdAndStatusAndIsDeletedFalse(ID, DurableMedicalEquipmentEnum.RETIRED.toString()))
                .thenReturn(Optional.empty());
        when(repository.findById(ID)).thenReturn(Optional.of(mockEntity));
        when(repository.save(any())).thenReturn(mockEntity);

        service.patchEquipmentStatus(ID, request);

        assertEquals(DurableMedicalEquipmentEnum.IN_SERVICE, mockEntity.getStatus());
        verify(repository).save(mockEntity);
    }

    @Test
    void patchEquipmentStatus_Fails_WhenRetired() {
        DurableMedicalEquipmentChangeStatusRequest request = new DurableMedicalEquipmentChangeStatusRequest();
        request.setStatus("AVAILABLE");

        // Giả lập thiết bị đã RETIRED
        when(repository.findByIdAndStatusAndIsDeletedFalse(ID, DurableMedicalEquipmentEnum.RETIRED.toString()))
                .thenReturn(Optional.of(mockEntity));

        assertThrows(RuntimeException.class, () -> service.patchEquipmentStatus(ID, request));
    }
}