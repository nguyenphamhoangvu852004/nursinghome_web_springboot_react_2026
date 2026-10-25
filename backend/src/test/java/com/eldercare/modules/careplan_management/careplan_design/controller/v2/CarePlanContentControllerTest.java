package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.modules.careplan_management.careplan_design.entity.v2.*;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CarePlanContentControllerTest {
    private final CarePlanRepository plans = mock(CarePlanRepository.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new CarePlanContentController(plans)).build();

    @AfterEach
    void clearSession() { SecurityContextHolder.clearContext(); }

    private void signIn() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("nurse@test.local", null, List.of()));
    }

    @Test
    void requiresAuthenticationAndValidPlanId() throws Exception {
        mvc.perform(get("/api/v2/care-plans/1/content")).andExpect(status().isUnauthorized());
        signIn();
        mvc.perform(get("/api/v2/care-plans/0/content")).andExpect(status().isBadRequest());
        verifyNoInteractions(plans);
        when(plans.findById(1L)).thenReturn(Optional.empty());
        mvc.perform(get("/api/v2/care-plans/1/content")).andExpect(status().isNotFound());
    }

    @Test
    void returnsNestedContentAndDateOnlyFieldsWithoutJpaRelations() throws Exception {
        signIn();
        var intervention = new CareIntervention();
        intervention.setId(21L);
        intervention.setCareGoalId(11L);
        intervention.setTitle("Assist transfer");
        intervention.setStartDate(Date.valueOf("2026-10-23"));
        var goal = new CareGoal();
        goal.setId(11L);
        goal.setCarePlanVersionId(10L);
        goal.setTitle("Safe transfer");
        goal.setTargetDate(Date.valueOf("2026-11-15"));
        goal.setInterventions(List.of(intervention));
        var older = CarePlanVersion.builder().id(9L).versionNumber(1).status("EFFECTIVE").build();
        var draft = CarePlanVersion.builder().id(10L).versionNumber(2).status("DRAFT").goals(List.of(goal)).build();
        var plan = CarePlan.builder().id(1L).state("OPEN").versions(List.of(older, draft)).build();
        when(plans.findById(1L)).thenReturn(Optional.of(plan));
        mvc.perform(get("/api/v2/care-plans/1/content"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.state").value("OPEN"))
                .andExpect(jsonPath("$.data.versions[0].id").value(10))
                .andExpect(jsonPath("$.data.versions[0].goals[0].goal.targetDate").value("2026-11-15"))
                .andExpect(jsonPath("$.data.versions[0].goals[0].interventions[0].startDate").value("2026-10-23"))
                .andExpect(jsonPath("$.data.versions[0].goals[0].interventions[0].careGoal").doesNotExist());
    }
}
