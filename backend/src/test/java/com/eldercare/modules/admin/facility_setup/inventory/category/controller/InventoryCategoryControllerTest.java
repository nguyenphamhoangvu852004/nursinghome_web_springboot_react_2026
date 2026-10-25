package com.eldercare.modules.admin.facility_setup.inventory.category.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eldercare.common.constants.RouteConstants;
import com.eldercare.common.dto.PagedResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.request.InventoryCategoryRequest;
import com.eldercare.modules.admin.facility_setup.inventory.category.dto.response.InventoryCategoryResponse;
import com.eldercare.modules.admin.facility_setup.inventory.category.service.InventoryCategoryServiceInterface;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.security.SessionStore;

import tools.jackson.databind.ObjectMapper;


// @WebMvcTest chỉ khởi tạo môi trường Web cho đúng Controller này, giúp test chạy cực nhanh
@WebMvcTest(InventoryCategoryController.class)
@ActiveProfiles("test")
class InventoryCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Công cụ dùng để chuyển Object thành chuỗi JSON

    @MockitoBean
    private SessionStore sessionStore;

    @MockitoBean // Thêm dòng này để đánh lừa hàm activateAdmin
    private UserRepository userRepository;

    @MockitoBean
    private InventoryCategoryServiceInterface inventoryCategoryService;

    // Giả sử RouteConstants.API_ADMIN_INVENTORY_CATEGORIES = "/api/admin/inventory-categories"
    // Hãy import biến này từ class RouteConstants của bạn.
    private static final String BASE_URL = RouteConstants.API_ADMIN_INVENTORY_CATEGORIES;
    private final long CATEGORY_ID = 1L;

    private InventoryCategoryRequest validRequest;
    private InventoryCategoryResponse mockResponse;

    @BeforeEach
    void setUp() {
        validRequest = new InventoryCategoryRequest();
        validRequest.setCategoryName("Medical Supplies");
        validRequest.setDescription("Basic medical tools");

        mockResponse = InventoryCategoryResponse.builder()
                .id(CATEGORY_ID)
                .categoryName("Medical Supplies")
                .description("Basic medical tools")
                .build();
    }

    // ==========================================
    // TEST METHOD: GET ALL
    // ==========================================
    @Test
    void getAllInventoryCategories_Returns200AndPagedResponse() throws Exception {
        // Arrange
        int page = 0;
        int size = 10;
        PagedResponse<List<InventoryCategoryResponse>> pagedResponse = PagedResponse.of(
                List.of(mockResponse), 200, "Success", page, 1, size, 1
        );

        when(inventoryCategoryService.getInventoryCategoryList(size, page)).thenReturn(pagedResponse);

        // Act & Assert
        mockMvc.perform(get(BASE_URL)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                // Kiểm tra dựa trên cấu trúc PagedResponse của bạn
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data[0].id").value(CATEGORY_ID))
                // Chú ý: Dùng "category_name" vì bạn đã config @JsonProperty trong Response
                .andExpect(jsonPath("$.data[0].category_name").value("Medical Supplies")) 
                .andExpect(jsonPath("$.metadata.currentPage").value(1));
    }

    // ==========================================
    // TEST METHOD: POST (Create)
    // ==========================================
    @Test
    void createInventoryCategory_ValidRequest_Returns201() throws Exception {
        // Arrange
        when(inventoryCategoryService.createInventoryCategory(any(InventoryCategoryRequest.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        // Biến object validRequest thành chuỗi JSON body
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated()) // HTTP 201
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.category_name").value("Medical Supplies"));
    }

    @Test
    void createInventoryCategory_InvalidRequest_Returns400() throws Exception {
        // Arrange
        InventoryCategoryRequest invalidRequest = new InventoryCategoryRequest();
        // Cố tình bỏ trống CategoryName để vi phạm @NotBlank
        invalidRequest.setDescription("Missing name");

        // Act & Assert
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest()); // Kích hoạt @Valid, trả về HTTP 400
    }

    // ==========================================
    // TEST METHOD: GET BY ID
    // ==========================================
    @Test
    void getInventoryCategoryById_Returns200() throws Exception {
        // Arrange
        when(inventoryCategoryService.getInventoryCategoryById(CATEGORY_ID)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get(BASE_URL + "/{categoryId}", CATEGORY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.category_name").value("Medical Supplies"));
    }

    // ==========================================
    // TEST METHOD: PUT (Update)
    // ==========================================
    @Test
    void updateInventoryCategory_ValidRequest_Returns200() throws Exception {
        // Arrange
        when(inventoryCategoryService.updateInventoryCategory(eq(CATEGORY_ID), any(InventoryCategoryRequest.class)))
                .thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(put(BASE_URL + "/{categoryId}", CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(CATEGORY_ID))
                .andExpect(jsonPath("$.category_name").value("Medical Supplies"));
    }

    // ==========================================
    // TEST METHOD: DELETE
    // ==========================================
    @Test
    void deleteInventoryCategory_Returns204() throws Exception {
        // Arrange
        // Dùng doNothing() vì hàm delete() trong Service là kiểu void (không trả về gì cả)
        doNothing().when(inventoryCategoryService).deleteInventoryCategory(CATEGORY_ID);

        // Act & Assert
        mockMvc.perform(delete(BASE_URL + "/{categoryId}", CATEGORY_ID))
                .andExpect(status().isNoContent()); // Kì vọng HTTP 204 No Content
    }
}