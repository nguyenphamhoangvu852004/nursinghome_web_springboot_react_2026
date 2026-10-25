package com.eldercare.modules.resident_intake.assessment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentCreateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentDecisionRequest;
import com.eldercare.modules.resident_intake.assessment.dto.request.AssessmentUpdateRequest;
import com.eldercare.modules.resident_intake.assessment.dto.response.AssessmentResponse;
import com.eldercare.modules.resident_intake.assessment.service.AssessmentService;
import com.eldercare.modules.resident_intake.assessment_detail.dto.request.AssessmentDetailRequest;
import com.eldercare.modules.resident_intake.care_level.history.dto.response.LocClassificationResultResponse;

import tools.jackson.databind.ObjectMapper;

// @WebMvcTest(AssessmentController.class)
public class AssessmentControllerTests {
  @Autowired
  private MockMvc mockMvc;
  @MockitoBean
  private AssessmentService service;
  @Autowired
  private ObjectMapper objectMapper;

  @Test // valid assessment creation -> 201 CREATED
  void create_validRequest_returns201() throws Exception {
    AssessmentDetailRequest d = new AssessmentDetailRequest();
    d.setMetricId(1L);
    d.setScore(70);
    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(1L);
    req.setDetails(List.of(d));

    AssessmentResponse res = new AssessmentResponse();
    res.setId(1L);
    res.setStatus("DRAFT");
    res.setAdlTotalScore(70);

    when(service.create(any())).thenReturn(res);

    mockMvc.perform(post("/api/v1/assessments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.data.adlTotalScore").value(70));
  }

  @Test // invalid admissionId -> service throws exception -> error response
  void create_invalidAdmission_propagatesError() throws Exception {
    when(service.create(any())).thenThrow(new RuntimeException("Admission not found"));

    AssessmentCreateRequest req = new AssessmentCreateRequest();
    req.setAdmissionId(999L);
    req.setDetails(List.of());

    mockMvc.perform(post("/api/v1/assessments")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().is5xxServerError());
  }

  @Test // valid update -> 200 OK
  void update_validRequest_returns200() throws Exception {
    AssessmentUpdateRequest req = new AssessmentUpdateRequest();
    req.setDetails(List.of());
    AssessmentResponse res = new AssessmentResponse();
    res.setId(1L);
    res.setStatus("DRAFT");

    when(service.update(eq(1L), any())).thenReturn(res);

    mockMvc.perform(put("/api/v1/assessments/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk());
  }

  @Test // valid decision -> 200 OK
  void decide_validRequest_returns200() throws Exception {
    AssessmentDecisionRequest req = new AssessmentDecisionRequest();
    req.setStatus("COMPLETED");
    req.setConfirmedCareLevelId(1L);

    AssessmentResponse res = new AssessmentResponse();
    res.setId(1L);
    res.setStatus("COMPLETED");

    when(service.decide(eq(1L), any())).thenReturn(res);

    mockMvc.perform(put("/api/v1/assessments/1/decision")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(req)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("COMPLETED"));
  }

  @Test // valid residentId with active assessment -> 200 OK
  void getClassificationResult_found_returns200() throws Exception {
    LocClassificationResultResponse res = LocClassificationResultResponse.builder()
        .residentId(1L).adlTotalScore(60).build();

    when(service.getClassificationResult(1L)).thenReturn(res);

    mockMvc.perform(get("/api/v1/assessments/resident/1/classification-result"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.adlTotalScore").value(60));
  }

  @Test // residentId with no active assessment -> throws exception
  void getClassificationResult_notFound_propagatesError() throws Exception {
    when(service.getClassificationResult(99L))
        .thenThrow(new RuntimeException("not found"));

    mockMvc.perform(get("/api/v1/assessments/resident/99/classification-result"))
        .andExpect(status().is5xxServerError());
  }

  @Test // valid DELETE draft -> 200 OK
  void delete_validId_returns200() throws Exception {
    doNothing().when(service).deleteDraft(1L);
    mockMvc.perform(delete("/api/v1/assessments/1"))
        .andExpect(status().isOk());
  }

}
