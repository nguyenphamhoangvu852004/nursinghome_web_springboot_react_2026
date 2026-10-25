package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal.CreateCareGoalRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CareGoalService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CareGoalControllerTest {
    private final CareGoalService service = mock(CareGoalService.class);
    private final UserRepository users = mock(UserRepository.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new CareGoalController(service, users)).build();
    private static final String URL = "/api/v2/care-plan-versions/10/goals";
    private static final String BODY = """
            {"problemOrNeed":"Need", "title":"Transfer", "desiredOutcome":"Outcome",
             "baselineValue":"Baseline", "targetDate":"2026-11-15", "actorId":999,
             "carePlanVersionId":999, "status":"ACHIEVED"}
            """;

    @AfterEach
    void clearSession() { SecurityContextHolder.clearContext(); }

    private void signIn() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("nurse@test.local", null,
                        List.of(new SimpleGrantedAuthority("ROLE_Nurse"))));
        var user = new UserEntity();
        user.setId(72L);
        when(users.findByEmailAndIsDeletedFalse("nurse@test.local")).thenReturn(Optional.of(user));
    }

    @Test
    void createsWithPathVersionAndSessionActorAndDateOnlyResponse() throws Exception {
        signIn();
        when(service.createGoal(any())).thenAnswer(call -> {
            CreateCareGoalRequest request = call.getArgument(0);
            var goal = new CareGoal();
            goal.setId(101L);
            goal.setCarePlanVersionId(request.carePlanVersionId());
            goal.setCreatedBy(request.actorId());
            goal.setStatus("ACTIVE");
            return goal;
        });
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", URL + "/101"))
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.carePlanVersionId").value(10))
                .andExpect(jsonPath("$.data.createdBy").value(72))
                .andExpect(jsonPath("$.data.targetDate").value("2026-11-15"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"));
        var captured = ArgumentCaptor.forClass(CreateCareGoalRequest.class);
        verify(service).createGoal(captured.capture());
        assertEquals("2026-11-15", ((java.sql.Date) captured.getValue().targetDate()).toLocalDate().toString());
    }

    @Test
    void requiresSignedInExistingUser() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());
        signIn();
        when(users.findByEmailAndIsDeletedFalse(anyString())).thenReturn(Optional.empty());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());
        verifyNoInteractions(service);
    }

    @Test
    void rejectsRolesWithoutCareGoalPermission() throws Exception {
        signIn();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("nurse@test.local", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CNA"))));
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }

    @Test
    void rejectsInvalidIdsAndMalformedDateBeforeSaving() throws Exception {
        signIn();
        mvc.perform(post("/api/v2/care-plan-versions/0/goals").contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isBadRequest());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY.replace("2026-11-15", "invalid")))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void mapsServiceFailuresToHttpStatuses() throws Exception {
        signIn();
        doThrow(new IllegalArgumentException("Title is required")).when(service).createGoal(any());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isBadRequest());
        doThrow(new NotFoundException("Version not found")).when(service).createGoal(any());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isNotFound());
        doThrow(new IllegalStateException("Version must be DRAFT")).when(service).createGoal(any());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isConflict());
    }
}
