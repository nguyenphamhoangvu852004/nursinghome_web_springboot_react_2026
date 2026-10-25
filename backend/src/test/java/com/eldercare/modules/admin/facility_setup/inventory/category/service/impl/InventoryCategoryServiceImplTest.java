package com.eldercare.modules.admin.facility_setup.inventory.category.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

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
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.mappper.InventoryCategoryMapper;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.request.InventoryCategoryRequest;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.response.InventoryCategoryResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.entity.InventoryCategoryEntity;
import com.eldercare.modules.admin.facility_setup.inventory.category.repository.InventoryCategoryRepository;

@ExtendWith(MockitoExtension.class)
class InventoryCategoryServiceImplTest {

    @Mock
    private InventoryCategoryRepository inventoryCategoryRepository;

    @Mock
    private InventoryCategoryMapper inventoryCategoryMapper;

    @InjectMocks
    private InventoryCategoryServiceImpl inventoryCategoryService;

    private InventoryCategoryEntity mockEntity;
    private InventoryCategoryRequest mockRequest;
    private InventoryCategoryResponse mockResponse;
    
    private final long CATEGORY_ID = 1L;

    @BeforeEach
    void setUp() {
        // --- 1. Dùng @Builder từ class InventoryCategoryEntity của bạn ---
        mockEntity = InventoryCategoryEntity.builder()
                .id(CATEGORY_ID)
                .categoryName("Medical Supplies")
                .description("Basic medical tools")
                .isDeleted(false) // Dựa theo @Builder.Default trong code của bạn
                .build();

        // --- 2. Dùng hàm setter cho Request (vì class này chỉ có @Data) ---
        mockRequest = new InventoryCategoryRequest();
        mockRequest.setCategoryName("Medical Supplies");
        mockRequest.setDescription("Basic medical tools");

        // --- 3. Dùng @Builder cho Response ---
        mockResponse = InventoryCategoryResponse.builder()
                .id(CATEGORY_ID)
                .categoryName("Medical Supplies")
                .description("Basic medical tools")
                .build();
    }

    // ==========================================
    // TEST METHOD: getInventoryCategoryList
    // ==========================================
    @Test
    void getInventoryCategoryList_Success() {
        // Arrange
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<InventoryCategoryEntity> mockPage = new PageImpl<>(List.of(mockEntity), pageable, 1);
        
        when(inventoryCategoryRepository.findAll(pageable)).thenReturn(mockPage);
        when(inventoryCategoryMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        // Act
        PagedResponse<List<InventoryCategoryResponse>> result = inventoryCategoryService.getInventoryCategoryList(size, page);

        // Assert
        assertNotNull(result);
        assertEquals(200, result.getStatusCode()); // Dựa theo ApiResponse của bạn
        assertEquals("Inventory categories retrieved successfully", result.getMessage());
        
        // Kiểm tra dữ liệu
        assertEquals(1, result.getData().size());
        assertEquals("Medical Supplies", result.getData().get(0).getCategoryName());
        
        // Kiểm tra phần phân trang (PaginationMetadata)
        assertNotNull(result.getMetadata());
        assertEquals(1, result.getMetadata().getCurrentPage()); // Logic page + 1 của bạn
        assertEquals(size, result.getMetadata().getCurrentLimit());
        assertEquals(1, result.getMetadata().getTotalElements());
        
        verify(inventoryCategoryRepository, times(1)).findAll(pageable);
    }

    // ==========================================
    // TEST METHOD: createInventoryCategory
    // ==========================================
    @Test
    void createInventoryCategory_Success() {
        // Arrange
        when(inventoryCategoryMapper.toEntity(mockRequest)).thenReturn(mockEntity);
        when(inventoryCategoryRepository.save(mockEntity)).thenReturn(mockEntity);
        when(inventoryCategoryMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        // Act
        InventoryCategoryResponse result = inventoryCategoryService.createInventoryCategory(mockRequest);

        // Assert
        assertNotNull(result);
        assertEquals(CATEGORY_ID, result.getId());
        assertEquals("Medical Supplies", result.getCategoryName());
        
        verify(inventoryCategoryRepository, times(1)).save(mockEntity);
    }

    // ==========================================
    // TEST METHOD: getInventoryCategoryById
    // ==========================================
    @Test
    void getInventoryCategoryById_Found_ReturnsResponse() {
        // Arrange
        when(inventoryCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(mockEntity));
        when(inventoryCategoryMapper.toResponse(mockEntity)).thenReturn(mockResponse);

        // Act
        InventoryCategoryResponse result = inventoryCategoryService.getInventoryCategoryById(CATEGORY_ID);

        // Assert
        assertNotNull(result);
        assertEquals(CATEGORY_ID, result.getId());
    }

    @Test
    void getInventoryCategoryById_NotFound_ThrowsException() {
        // Arrange
        when(inventoryCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> inventoryCategoryService.getInventoryCategoryById(CATEGORY_ID));
        
        assertEquals("Inventory category not found with id: " + CATEGORY_ID, exception.getMessage());
        verify(inventoryCategoryMapper, never()).toResponse(any());
    }

    // ==========================================
    // TEST METHOD: updateInventoryCategory
    // ==========================================
    @Test
    void updateInventoryCategory_Found_UpdatesAndReturns() {
        // Arrange
        InventoryCategoryRequest updateRequest = new InventoryCategoryRequest();
        updateRequest.setCategoryName("Updated Supplies");
        updateRequest.setDescription("Updated Desc");

        // Tạo ra Entity giả lập sau khi đã save
        InventoryCategoryEntity savedEntity = InventoryCategoryEntity.builder()
                .id(CATEGORY_ID)
                .categoryName("Updated Supplies")
                .description("Updated Desc")
                .build();
                
        InventoryCategoryResponse updatedResponse = InventoryCategoryResponse.builder()
                .id(CATEGORY_ID)
                .categoryName("Updated Supplies")
                .description("Updated Desc")
                .build();

        when(inventoryCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(mockEntity));
        // Mockito sẽ nhận bất kỳ Entity nào được truyền vào hàm save
        when(inventoryCategoryRepository.save(any(InventoryCategoryEntity.class))).thenReturn(savedEntity);
        when(inventoryCategoryMapper.toResponse(savedEntity)).thenReturn(updatedResponse);

        // Act
        InventoryCategoryResponse result = inventoryCategoryService.updateInventoryCategory(CATEGORY_ID, updateRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Supplies", result.getCategoryName());
        assertEquals("Updated Desc", result.getDescription());
        
        // Xác minh entity đã được đổi giá trị thành công trước khi save
        assertEquals("Updated Supplies", mockEntity.getCategoryName()); 
        verify(inventoryCategoryRepository, times(1)).save(mockEntity);
    }

    @Test
    void updateInventoryCategory_NotFound_ThrowsException() {
        // Arrange
        when(inventoryCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> inventoryCategoryService.updateInventoryCategory(CATEGORY_ID, mockRequest));
            
        assertEquals("Inventory category not found with id: " + CATEGORY_ID, exception.getMessage());
        verify(inventoryCategoryRepository, never()).save(any()); 
    }

    // ==========================================
    // TEST METHOD: deleteInventoryCategory
    // ==========================================
    @Test
    void deleteInventoryCategory_Found_DeletesSuccessfully() {
        // Arrange
        when(inventoryCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.of(mockEntity));

        // Act
        inventoryCategoryService.deleteInventoryCategory(CATEGORY_ID);

        // Assert
        verify(inventoryCategoryRepository, times(1)).delete(mockEntity);
    }

    @Test
    void deleteInventoryCategory_NotFound_ThrowsException() {
        // Arrange
        when(inventoryCategoryRepository.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> inventoryCategoryService.deleteInventoryCategory(CATEGORY_ID));
            
        assertEquals("Inventory category not found with id: " + CATEGORY_ID, exception.getMessage());
        verify(inventoryCategoryRepository, never()).delete(any());
    }
}