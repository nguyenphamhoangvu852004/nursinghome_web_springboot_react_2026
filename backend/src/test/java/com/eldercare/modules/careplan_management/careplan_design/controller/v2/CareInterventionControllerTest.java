package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.UserEntity;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareIntervention;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareGoalRepository;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CareInterventionRepository;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CareInterventionService;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CarePlanVersionService;
import org.junit.jupiter.api.*;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Exercises HTTP binding through the real service with persistence mocked. */
class CareInterventionControllerTest {
    private final CareInterventionRepository interventions = mock(CareInterventionRepository.class);
    private final CareGoalRepository goals = mock(CareGoalRepository.class);
    private final CarePlanVersionService versions = mock(CarePlanVersionService.class);
    private final UserRepository users = mock(UserRepository.class);
    private final CareInterventionService service = new CareInterventionService(interventions, goals, versions);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new CareInterventionController(service, users)).build();
    private static final String URL = "/api/v2/care-goals/1/interventions";
    private static final String BODY = """
            {"title":"Assist transfer", "instructions":"Follow assessed plan", "assignedRole":"CNA",
             "frequencyCode":"DAILY", "startDate":"2026-10-23", "endDate":"2026-11-15",
             "actorId":999, "careGoalId":999, "id":999, "createdBy":999}
            """;

    @AfterEach
    void clearSession() { SecurityContextHolder.clearContext(); }

    private void signIn(String role) {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                "nurse@test.local", null, List.of(new SimpleGrantedAuthority("ROLE_" + role))));
        var user = new UserEntity();
        user.setId(72L);
        when(users.findByEmailAndIsDeletedFalse("nurse@test.local")).thenReturn(Optional.of(user));
    }

    private void draft() {
        var goal = new CareGoal();
        goal.setCarePlanVersionId(10L);
        when(goals.findById(1L)).thenReturn(Optional.of(goal));
        when(versions.findById(10L)).thenReturn(CarePlanVersion.builder().id(10L).status("DRAFT").build());
        when(interventions.save(any())).thenAnswer(call -> {
            CareIntervention intervention = call.getArgument(0);
            intervention.setId(101L);
            return intervention;
        });
    }

    @Test
    void createsUsingPathAndSessionWithDefaultsAndDateOnlyOutput() throws Exception {
        signIn("Nurse");
        draft();
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value(201))
                .andExpect(jsonPath("$.data.id").value(101))
                .andExpect(jsonPath("$.data.careGoalId").value(1))
                .andExpect(jsonPath("$.data.createdBy").value(72))
                .andExpect(jsonPath("$.data.updatedBy").value(72))
                .andExpect(jsonPath("$.data.createdAt").exists())
                .andExpect(jsonPath("$.data.startDate").value("2026-10-23"))
                .andExpect(jsonPath("$.data.endDate").value("2026-11-15"))
                .andExpect(jsonPath("$.data.requiresTaskGeneration").value(true))
                .andExpect(jsonPath("$.data.sortOrder").value(0))
                .andExpect(jsonPath("$.data.careGoal").doesNotExist());
    }

    @Test
    void donCanPreserveFalseAndSortOrderWithOptionalDatesOmitted() throws Exception {
        signIn("DON");
        draft();
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content("""
                {"title":"Instructions", "instructions":"Follow plan", "assignedRole":"CNA",
                 "frequencyCode":"DAILY", "requiresTaskGeneration":false, "sortOrder":10}
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.requiresTaskGeneration").value(false))
                .andExpect(jsonPath("$.data.sortOrder").value(10))
                .andExpect(jsonPath("$.data.startDate").isEmpty());
    }

    @Test
    void rejectsAnonymousMissingUserAndUnauthorizedRole() throws Exception {
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());
        signIn("Nurse");
        when(users.findByEmailAndIsDeletedFalse(anyString())).thenReturn(Optional.empty());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isUnauthorized());
        signIn("CNA");
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isForbidden());
        verifyNoInteractions(interventions, goals, versions);
    }

    @Test
    void rejectsInvalidIdsMalformedDatesMissingBodyAndInvalidContent() throws Exception {
        signIn("Nurse");
        for (String id : List.of("0", "-1", "abc")) {
            mvc.perform(post("/api/v2/care-goals/" + id + "/interventions")
                    .contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isBadRequest());
        }
        for (String body : List.of("", "null", "{}", BODY.replace("2026-11-15", "invalid"),
                BODY.replace("2026-11-15", "2026-10-22"), BODY.replace("Assist transfer", " "),
                BODY.replace("Assist transfer", "x".repeat(256)))) {
            mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(interventions, goals, versions);
    }

    @Test
    void rejectsMissingGoalMissingVersionAndNonDraftVersion() throws Exception {
        signIn("Nurse");
        when(goals.findById(1L)).thenReturn(Optional.empty());
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isNotFound());
        var goal = new CareGoal();
        goal.setCarePlanVersionId(10L);
        when(goals.findById(1L)).thenReturn(Optional.of(goal));
        when(versions.findById(10L)).thenThrow(new NotFoundException("Version not found"));
        mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isNotFound());
        for (String state : List.of("PENDING_APPROVAL", "EFFECTIVE", "SUPERSEDED")) {
            doReturn(CarePlanVersion.builder().id(10L).status(state).build()).when(versions).findById(10L);
            mvc.perform(post(URL).contentType(MediaType.APPLICATION_JSON).content(BODY)).andExpect(status().isConflict());
        }
        verifyNoInteractions(interventions);
    }
}
