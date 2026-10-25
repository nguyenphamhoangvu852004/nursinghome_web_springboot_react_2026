package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.service.impl;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

@ExtendWith(MockitoExtension.class)
class ConsumableSupplyServiceImplTest {

    @Mock
    private ConsumableSupplyRepository consumableSupplyRepository;
    @Mock
    private ConsumableSupplyMapper consumableSupplyMapper;
    @Mock
    private InventoryCategoryRepository inventoryCategoryRepository;
    @Mock
    private FacilityRepository facilityRepository;

    @InjectMocks
    private ConsumableSupplyServiceImpl consumableSupplyService;

    private ConsumableSupplyEntity mockEntity;
    private ConsumableSupplyCreateRequest mockCreateRequest;
    private ConsumableSupplyUpdateRequest mockUpdateRequest;
    private ConsumableSupplyResponse mockResponse;
    private InventoryCategoryEntity mockCategory;
    private FacilityEntity mockFacility;

    private final long SUPPLY_ID = 1L;
    private final long CATEGORY_ID = 100L;
    private final long FACILITY_ID = 200L;

    // Dùng để test System.out.println
    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        // Cấu hình để chụp log từ System.out
        System.setOut(new PrintStream(outputStreamCaptor));

        // Khởi tạo Category (Giả định có setter setId)
        mockCategory = new InventoryCategoryEntity();
        // mockCategory.setId(CATEGORY_ID); 

        // Khởi tạo FacilityEntity (Dùng Builder theo code bạn cung cấp)
        mockFacility = FacilityEntity.builder()
                .id(FACILITY_ID)
                .name("Test Facility")
                .isDeleted(false)
                .build();

        // Khởi tạo ConsumableSupplyEntity (Dùng Builder)
        mockEntity = ConsumableSupplyEntity.builder()
                .id(SUPPLY_ID)
                .itemName("Medical Gloves")
                .category(mockCategory)
                .facility(mockFacility)
                .stockOnHand(100)
                .total(100)
                .reorderThreshold(20)
                .unitCost(new BigDecimal("15.50"))
                .privatePayRate(new BigDecimal("20.00"))
                .status(ConsumableSupplyEnum.OK)
                .isDeleted(false)
                .build();

        // Khởi tạo ConsumableSupplyCreateRequest (Không có Builder, dùng Setter)
        mockCreateRequest = new ConsumableSupplyCreateRequest();
        mockCreateRequest.setItemName("Medical Gloves");
        mockCreateRequest.setCategoryId(CATEGORY_ID);
        mockCreateRequest.setFacilityId(FACILITY_ID);
        mockCreateRequest.setStockOnHand(100);
        mockCreateRequest.setReorderThreshold(20);
        mockCreateRequest.setUnitCost(new BigDecimal("15.50"));
        mockCreateRequest.setPrivatePayRate(new BigDecimal("20.00"));

        // Khởi tạo ConsumableSupplyUpdateRequest (Không có Builder, dùng Setter)
        mockUpdateRequest = new ConsumableSupplyUpdateRequest();
        mockUpdateRequest.setItemName("Updated Gloves");
        mockUpdateRequest.setCategoryId(CATEGORY_ID);
        mockUpdateRequest.setFacilityId(FACILITY_ID);
        mockUpdateRequest.setReorderThreshold(30);
        mockUpdateRequest.setUnitCost(new BigDecimal("16.00"));
        mockUpdateRequest.setPrivatePayRate(new BigDecimal("22.00"));

        // Khởi tạo ConsumableSupplyResponse (Không có Builder, dùng Setter)
        mockResponse = new ConsumableSupplyResponse();
        mockResponse.setId(SUPPLY_ID);
        mockResponse.setItemName("Medical Gloves");
        mockResponse.setStockOnHand(100);
        mockResponse.setTotal(100);
        mockResponse.setReorderThreshold(new BigDecimal("20")); // DTO của bạn khai báo là BigDecimal
        mockResponse.setStatus(ConsumableSupplyEnum.OK);
    }

    @AfterEach
    void tearDown() {
        // Trả lại luồng System.out bình thường sau mỗi test
        System.setOut(standardOut);
    }

    @Test
    void getAllConsumableSupplies_Success_And_PrintsToConsole() {
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsumableSupplyEntity> mockPage = new PageImpl<>(List.of(mockEntity), pageable, 1);

        when(consumableSupplyRepository.findAll(pageable)).thenReturn(mockPage);
        when(consumableSupplyMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        PagedResponse<List<ConsumableSupplyResponse>> result = consumableSupplyService.getAllConsumableSupplies(page, size);

        // 1. Verify dữ liệu trả về
        assertNotNull(result);
        assertEquals(200, result.getStatusCode());
        assertEquals(1, result.getData().size());
        assertEquals("Medical Gloves", result.getData().get(0).getItemName());

        // 2. Verify System.out.println("Content: " + content)
        assertTrue(outputStreamCaptor.toString().contains("Content:"));
    }

    @Test
    void createConsumableSupply_StatusOk_Success() {
        when(consumableSupplyMapper.toEntity(mockCreateRequest)).thenReturn(mockEntity);
        when(inventoryCategoryRepository.getReferenceById(CATEGORY_ID)).thenReturn(mockCategory);
        when(facilityRepository.getReferenceById(FACILITY_ID)).thenReturn(mockFacility);
        when(consumableSupplyRepository.save(mockEntity)).thenReturn(mockEntity);
        when(consumableSupplyMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        ConsumableSupplyResponse result = consumableSupplyService.createConsumableSupply(mockCreateRequest);

        assertNotNull(result);
        assertEquals(ConsumableSupplyEnum.OK, mockEntity.getStatus());
        assertEquals(100, mockEntity.getTotal());
        verify(consumableSupplyRepository).save(mockEntity);
    }

    @Test
    void createConsumableSupply_StatusLowStock_Success() {
        mockCreateRequest.setStockOnHand(20);
        mockCreateRequest.setReorderThreshold(20); // <= Threshold

        when(consumableSupplyMapper.toEntity(mockCreateRequest)).thenReturn(mockEntity);
        when(inventoryCategoryRepository.getReferenceById(CATEGORY_ID)).thenReturn(mockCategory);
        when(facilityRepository.getReferenceById(FACILITY_ID)).thenReturn(mockFacility);
        when(consumableSupplyRepository.save(mockEntity)).thenReturn(mockEntity);
        when(consumableSupplyMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        consumableSupplyService.createConsumableSupply(mockCreateRequest);

        assertEquals(ConsumableSupplyEnum.LOW_STOCK, mockEntity.getStatus());
    }

    @Test
    void createConsumableSupply_StatusOutOfStock_Success() {
        mockCreateRequest.setStockOnHand(0);

        when(consumableSupplyMapper.toEntity(mockCreateRequest)).thenReturn(mockEntity);
        when(inventoryCategoryRepository.getReferenceById(CATEGORY_ID)).thenReturn(mockCategory);
        when(facilityRepository.getReferenceById(FACILITY_ID)).thenReturn(mockFacility);
        when(consumableSupplyRepository.save(mockEntity)).thenReturn(mockEntity);
        when(consumableSupplyMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        consumableSupplyService.createConsumableSupply(mockCreateRequest);

        assertEquals(ConsumableSupplyEnum.OUT_OF_STOCK, mockEntity.getStatus());
    }

    @Test
    void getLowStockSupplies_Success() {
        int page = 0, size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<ConsumableSupplyEntity> mockPage = new PageImpl<>(List.of(mockEntity), pageable, 1);

        when(consumableSupplyRepository.findByStatusAndIsDeletedFalse(ConsumableSupplyEnum.LOW_STOCK.toString(), pageable))
                .thenReturn(mockPage);
        when(consumableSupplyMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        PagedResponse<List<ConsumableSupplyResponse>> result = consumableSupplyService.getLowStockSupplies(page, size);

        assertNotNull(result);
        assertEquals(1, result.getData().size());
        verify(consumableSupplyRepository).findByStatusAndIsDeletedFalse(ConsumableSupplyEnum.LOW_STOCK.toString(), pageable);
    }

    @Test
    void getConsumableSupplyById_Found() {
        when(consumableSupplyRepository.findById(SUPPLY_ID)).thenReturn(Optional.of(mockEntity));
        when(consumableSupplyMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        ConsumableSupplyResponse result = consumableSupplyService.getConsumableSupplyById(SUPPLY_ID);
        
        assertNotNull(result);
        assertEquals(SUPPLY_ID, result.getId());
    }

    @Test
    void getConsumableSupplyById_NotFound_ThrowsException() {
        when(consumableSupplyRepository.findById(SUPPLY_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consumableSupplyService.getConsumableSupplyById(SUPPLY_ID));
    }

    @Test
    void updateConsumableSupply_Found_UpdatesSuccessfully() {
        when(consumableSupplyRepository.findById(SUPPLY_ID)).thenReturn(Optional.of(mockEntity));
        when(inventoryCategoryRepository.getReferenceById(CATEGORY_ID)).thenReturn(mockCategory);
        when(facilityRepository.getReferenceById(FACILITY_ID)).thenReturn(mockFacility);
        when(consumableSupplyRepository.save(any(ConsumableSupplyEntity.class))).thenReturn(mockEntity);
        when(consumableSupplyMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        ConsumableSupplyResponse result = consumableSupplyService.updateConsumableSupply(SUPPLY_ID, mockUpdateRequest);

        assertNotNull(result);
        assertEquals("Updated Gloves", mockEntity.getItemName());
        assertEquals(30, mockEntity.getReorderThreshold());
        assertEquals(new BigDecimal("16.00"), mockEntity.getUnitCost());
        verify(consumableSupplyRepository).save(mockEntity);
    }

    @Test
    void updateConsumableSupply_NotFound_ThrowsException() {
        when(consumableSupplyRepository.findById(SUPPLY_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, 
            () -> consumableSupplyService.updateConsumableSupply(SUPPLY_ID, mockUpdateRequest));
        verify(consumableSupplyRepository, never()).save(any());
    }

    @Test
    void deleteConsumableSupply_Found_SetsIsDeletedToTrue() {
        when(consumableSupplyRepository.findById(SUPPLY_ID)).thenReturn(Optional.of(mockEntity));

        consumableSupplyService.deleteConsumableSupply(SUPPLY_ID);

        assertTrue(mockEntity.getIsDeleted());
        verify(consumableSupplyRepository).save(mockEntity);
        verify(consumableSupplyRepository, never()).delete(any());
    }

    @Test
    void deleteConsumableSupply_NotFound_ThrowsException() {
        when(consumableSupplyRepository.findById(SUPPLY_ID)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> consumableSupplyService.deleteConsumableSupply(SUPPLY_ID));
        verify(consumableSupplyRepository, never()).save(any());
    }
}