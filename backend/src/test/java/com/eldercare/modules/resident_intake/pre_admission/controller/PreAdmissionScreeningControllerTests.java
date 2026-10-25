package com.eldercare.modules.resident_intake.pre_admission.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreCreateRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.request.PreDecisionRequest;
import com.eldercare.modules.resident_intake.pre_admission.dto.response.PreResponse;
import com.eldercare.modules.resident_intake.pre_admission.service.PreAdmissionScreeningService;
import com.eldercare.modules.resident_intake.resident.service.ResidentService;

import tools.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

// @WebMvcTest(PreAdmissionScreeningController.class)
public class PreAdmissionScreeningControllerTests {

  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private PreAdmissionScreeningService service;
  @MockitoBean
  private ResidentService residentService;
  @Autowired
  private ObjectMapper objectMapper;

  @Test // valid request -> 201 CREATED
  void create_validRequest_returns201() throws Exception {
    PreCreateRequest req = new PreCreateRequest();
    req.setResidentId(1L);
    PreResponse res = new PreResponse();
    res.setId(1L);
    res.setStatus("DRAFT");

    when(service.create(any())).thenReturn(res);

    mockMvc.perform(post("/api/v1/pre-admissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.status").value("DRAFT"));
  }

  @Test // residentId is null (missing required data) -> service throws exception ->
        // error response
  void create_missingResidentId_serviceThrows_propagatesError() throws Exception {
    PreCreateRequest req = new PreCreateRequest();

    when(service.create(any())).thenThrow(new RuntimeException("Resident not found"));

    mockMvc.perform(post("/api/v1/pre-admissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().is5xxServerError());
  }

  @Test // PUT decision with valid status -> 200 OK
  void decide_validStatus_returns200() throws Exception {
    PreDecisionRequest req = new PreDecisionRequest();
    req.setStatus("COMPLETED");
    PreResponse res = new PreResponse();
    res.setId(1L);
    res.setStatus("COMPLETED");

    when(service.decide(eq(1L), any())).thenReturn(res);

    mockMvc.perform(put("/api/v1/pre-admissions/1/decision")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("COMPLETED"));
  }

  @Test // DELETE successful -> 200 OK
  void delete_validId_returns200() throws Exception {
    doNothing().when(service).deleteDraft(1L);

    mockMvc.perform(delete("/api/v1/pre-admissions/1"))
        .andExpect(status().isOk());
  }

  @Test // negative ID (outside valid boundary for path variable)
  void delete_negativeId_stillReachesService() throws Exception {
    doNothing().when(service).deleteDraft(-1L);

    mockMvc.perform(delete("/api/v1/pre-admissions/-1"))
        .andExpect(status().isOk());
    verify(service).deleteDraft(-1L);
  }

  @Test // GET paged list -> 200 OK
  void list_returnsPagedResponse() throws Exception {
    Page<PreResponse> page = new PageImpl<>(List.of(new PreResponse()));
    when(service.listPaged(any())).thenReturn(page);

    mockMvc.perform(get("/api/v1/pre-admissions"))
        .andExpect(status().isOk());
  }
}
