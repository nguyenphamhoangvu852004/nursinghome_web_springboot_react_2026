package com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.request.*;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.dto.response.DurableMedicalEquipmentResponse;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.enums.DurableMedicalEquipmentEnum;
import com.eldercare.modules.admin.facility_setup.inventory.medicalequipment.service.DurableMedicalEquipmentServiceInterface;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.security.SessionStore;

import tools.jackson.databind.ObjectMapper;

@WebMvcTest(DurableMedicalEquipmentController.class)
@ActiveProfiles("test")
class DurableMedicalEquipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
      @MockitoBean
    private SessionStore sessionStore;

    @MockitoBean // Thêm dòng này để đánh lừa hàm activateAdmin
    private UserRepository userRepository;

    @MockitoBean
    private DurableMedicalEquipmentServiceInterface service;

    private DurableMedicalEquipmentResponse mockResponse;
    private final Long ID = 1L;

    @BeforeEach
    void setUp() {
        mockResponse = new DurableMedicalEquipmentResponse();
        mockResponse.setId(ID);
        mockResponse.setItemName("Wheelchair");
        mockResponse.setAssetTag("TAG-001");
        mockResponse.setStatus(DurableMedicalEquipmentEnum.AVAILABLE);
    }

    @Test
    void getEquipmentList_Returns200() throws Exception {
        PagedResponse<List<DurableMedicalEquipmentResponse>> pagedResponse = 
            PagedResponse.of(List.of(mockResponse), 200, "Success", 0, 1, 10, 1);

        when(service.getAllDurableMedicalEquipment(anyInt(), anyInt())).thenReturn(pagedResponse);

        mockMvc.perform(get(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].item_name").value("Wheelchair"));
    }

    @Test
    void createEquipment_ValidRequest_Returns201() throws Exception {
        DurableMedicalEquipmentCreateRequest request = new DurableMedicalEquipmentCreateRequest();
        request.setItemName("Wheelchair");
        request.setAssetTag("TAG-001");
        request.setCategoryId(1L);
        request.setFacilityId(1L);
        request.setUnitValue(new BigDecimal("500.00"));

        when(service.createEquipment(any())).thenReturn(mockResponse);

        mockMvc.perform(post(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.item_name").value("Wheelchair"));
    }

    @Test
    void createEquipment_InvalidRequest_Returns400() throws Exception {
        DurableMedicalEquipmentCreateRequest request = new DurableMedicalEquipmentCreateRequest();
        request.setItemName(""); // Invalid: @NotBlank

        mockMvc.perform(post(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEquipmentDetails_Returns200() throws Exception {
        when(service.getEquipmentById(ID)).thenReturn(mockResponse);

        mockMvc.perform(get(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT + "/{id}", ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(ID));
    }

    @Test
    void updateEquipment_Returns200() throws Exception {
        DurableMedicalEquipmentUpdateRequest request = new DurableMedicalEquipmentUpdateRequest();
        request.setItemName("Updated Wheelchair");
        request.setCategoryId(1L);
        request.setFacilityId(1L);
        request.setUnitValue(new BigDecimal("550.00"));

        when(service.updateEquipment(eq(ID), any())).thenReturn(mockResponse);

        mockMvc.perform(put(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT + "/{id}", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteEquipment_Returns204() throws Exception {
        doNothing().when(service).deleteEquipment(ID);

        mockMvc.perform(delete(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT + "/{id}", ID))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateEquipmentStatus_Returns200() throws Exception {
        DurableMedicalEquipmentChangeStatusRequest request = new DurableMedicalEquipmentChangeStatusRequest();
        request.setStatus("IN_SERVICE");

        when(service.patchEquipmentStatus(eq(ID), any())).thenReturn(mockResponse);

        mockMvc.perform(patch(RouteConstants.API_ADMIN_INVENTORY_MEDICAL_EQUIPMENT + "/{id}/status", ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}