package com.eldercare.modules.resident_intake.admission_ledger.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionCreateRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.request.AdmissionDischargeRequest;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionResponse;
import com.eldercare.modules.resident_intake.admission_ledger.dto.response.AdmissionSelectDTO;
import com.eldercare.modules.resident_intake.admission_ledger.service.AdmissionService;

import tools.jackson.databind.ObjectMapper;

// @WebMvcTest(AdmissionController.class)
public class AdmissionControllerTests {
  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private AdmissionService service;
  @Autowired
  private ObjectMapper objectMapper;

  @Test // valid admission creation -> 201 CREATED
  void create_validRequest_returns201() throws Exception {
    AdmissionCreateRequest req = new AdmissionCreateRequest();
    req.setPreAdmissionScreeningId(1L);
    req.setFacilityId(2L);
    req.setAdmissionDate(LocalDate.now());

    AdmissionResponse res = new AdmissionResponse();
    res.setId(1L);
    res.setStatus("ACTIVE");

    when(service.create(any())).thenReturn(res);

    mockMvc.perform(post("/api/v1/admissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.status").value("ACTIVE"));
  }

  @Test // invalid pre-admission -> service throws exception -> error response
  void create_invalidPre_propagatesError() throws Exception {
    when(service.create(any())).thenThrow(new RuntimeException("not eligible"));

    AdmissionCreateRequest req = new AdmissionCreateRequest();
    req.setPreAdmissionScreeningId(99L);

    mockMvc.perform(post("/api/v1/admissions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().is5xxServerError());
  }

  @Test // valid discharge -> 200 OK
  void discharge_validRequest_returns200() throws Exception {
    AdmissionDischargeRequest req = new AdmissionDischargeRequest();
    req.setDischargeDate(LocalDate.now());
    req.setDischargeReason("ok");

    AdmissionResponse res = new AdmissionResponse();
    res.setId(1L);
    res.setStatus("DISCHARGED");

    when(service.discharge(eq(1L), any())).thenReturn(res);

    mockMvc.perform(put("/api/v1/admissions/1/discharge")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("DISCHARGED"));
  }

  @Test // GET active-for-select list -> 200 OK
  void selectActive_returns200() throws Exception {
    when(service.listActiveForSelect()).thenReturn(List.of(new AdmissionSelectDTO()));

    mockMvc.perform(get("/api/v1/admissions/select-active"))
        .andExpect(status().isOk());
  }
}
