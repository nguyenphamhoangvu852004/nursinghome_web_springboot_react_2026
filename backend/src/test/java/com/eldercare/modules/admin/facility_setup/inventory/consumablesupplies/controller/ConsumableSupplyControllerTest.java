package com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
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
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyCreateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.request.ConsumableSupplyUpdateRequest;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.dto.response.ConsumableSupplyResponse;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.enums.ConsumableSupplyEnum;
import com.eldercare.modules.admin.facility_setup.inventory.consumablesupplies.service.ConsumableSupplyService;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.security.SessionStore;

import tools.jackson.databind.ObjectMapper;


@WebMvcTest(ConsumableSupplyController.class)
@ActiveProfiles("test")
class ConsumableSupplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

     @MockitoBean
    private SessionStore sessionStore;

    @MockitoBean // Thêm dòng này để đánh lừa hàm activateAdmin
    private UserRepository userRepository;

    @MockitoBean
    private ConsumableSupplyService consumableSupplyService;

    private ConsumableSupplyResponse mockResponse;
    private ConsumableSupplyCreateRequest validCreateRequest;
    private ConsumableSupplyUpdateRequest validUpdateRequest;

    private final Long SUPPLY_ID = 1L;

    @BeforeEach
    void setUp() {
        // Setup mock response
        mockResponse = new ConsumableSupplyResponse();
        mockResponse.setId(SUPPLY_ID);
        mockResponse.setItemName("Medical Gloves");
        mockResponse.setStockOnHand(100);
        mockResponse.setTotal(100);
        mockResponse.setReorderThreshold(new BigDecimal("20"));
        mockResponse.setUnitCost(new BigDecimal("15.50"));
        mockResponse.setPrivatePayRate(new BigDecimal("20.00"));
        mockResponse.setStatus(ConsumableSupplyEnum.OK);

        // Setup valid Create Request
        validCreateRequest = new ConsumableSupplyCreateRequest();
        validCreateRequest.setItemName("Medical Gloves");
        validCreateRequest.setCategoryId(100L);
        validCreateRequest.setFacilityId(200L);
        validCreateRequest.setStockOnHand(100);
        validCreateRequest.setReorderThreshold(20);
        validCreateRequest.setUnitCost(new BigDecimal("15.50"));
        validCreateRequest.setPrivatePayRate(new BigDecimal("20.00"));

        // Setup valid Update Request
        validUpdateRequest = new ConsumableSupplyUpdateRequest();
        validUpdateRequest.setItemName("Updated Gloves");
        validUpdateRequest.setCategoryId(100L);
        validUpdateRequest.setFacilityId(200L);
        validUpdateRequest.setReorderThreshold(30);
        validUpdateRequest.setUnitCost(new BigDecimal("16.00"));
        validUpdateRequest.setPrivatePayRate(new BigDecimal("22.00"));
    }

    // ==========================================
    // GET / 
    // ==========================================
    @Test
    void getAllConsumableSupplies_Returns200AndPagedData() throws Exception {
        PagedResponse<List<ConsumableSupplyResponse>> pagedResponse = PagedResponse.of(
                List.of(mockResponse), 200, "Success", 0, 1, 10, 1);

        when(consumableSupplyService.getAllConsumableSupplies(anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(SUPPLY_ID))
                .andExpect(jsonPath("$.data[0].item_name").value("Medical Gloves"))
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    // ==========================================
    // POST / 
    // ==========================================
    @Test
    void createConsumableSupply_ValidRequest_Returns201() throws Exception {
        when(consumableSupplyService.createConsumableSupply(any(ConsumableSupplyCreateRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(SUPPLY_ID))
                .andExpect(jsonPath("$.item_name").value("Medical Gloves"));
    }

    @Test
    void createConsumableSupply_InvalidRequest_Returns400() throws Exception {
        // Cố tình làm sai validation: Tên bị rỗng và số lượng âm
        validCreateRequest.setItemName(""); 
        validCreateRequest.setStockOnHand(-10);

        mockMvc.perform(post(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validCreateRequest)))
                .andExpect(status().isBadRequest()); 
                // Cần đảm bảo dự án của bạn có @ControllerAdvice xử lý MethodArgumentNotValidException trả về 400
    }

    // ==========================================
    // GET /low-stock
    // ==========================================
    @Test
    void getReorderSupplies_Returns200AndPagedData() throws Exception {
        mockResponse.setStatus(ConsumableSupplyEnum.LOW_STOCK);
        PagedResponse<List<ConsumableSupplyResponse>> pagedResponse = PagedResponse.of(
                List.of(mockResponse), 200, "Success", 0, 1, 10, 1);

        when(consumableSupplyService.getLowStockSupplies(anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES + "/low-stock")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value(ConsumableSupplyEnum.LOW_STOCK.toString()));
    }

    // ==========================================
    // GET /{supplyId}
    // ==========================================
    @Test
    void getConsumableSupplyDetails_Returns200() throws Exception {
        when(consumableSupplyService.getConsumableSupplyById(SUPPLY_ID)).thenReturn(mockResponse);

        mockMvc.perform(get(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES + "/{supplyId}", SUPPLY_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(SUPPLY_ID))
                .andExpect(jsonPath("$.item_name").value("Medical Gloves"));
    }

    // ==========================================
    // PUT /{supplyId}
    // ==========================================
    @Test
    void updateConsumableSupply_ValidRequest_Returns200() throws Exception {
        mockResponse.setItemName("Updated Gloves"); // Giả lập dữ liệu trả về đã được update

        when(consumableSupplyService.updateConsumableSupply(eq(SUPPLY_ID), any(ConsumableSupplyUpdateRequest.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(put(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES + "/{supplyId}", SUPPLY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.item_name").value("Updated Gloves"));
    }

    @Test
    void updateConsumableSupply_InvalidRequest_Returns400() throws Exception {
        // Truyền CategoryId âm để vi phạm @Positive
        validUpdateRequest.setCategoryId(-5L);

        mockMvc.perform(put(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES + "/{supplyId}", SUPPLY_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateRequest)))
                .andExpect(status().isBadRequest());
    }

    // ==========================================
    // DELETE /{supplyId}
    // ==========================================
    @Test
    void deleteConsumableSupply_Returns204NoContent() throws Exception {
        doNothing().when(consumableSupplyService).deleteConsumableSupply(SUPPLY_ID);

        mockMvc.perform(delete(RouteConstants.API_ADMIN_INVENTORY_CONSUMABLE_SUPPLIES + "/{supplyId}", SUPPLY_ID))
                .andExpect(status().isNoContent()); // HTTP 204
    }
}