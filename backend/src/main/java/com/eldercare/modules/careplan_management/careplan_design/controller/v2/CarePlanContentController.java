package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal.CreateCareGoalResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareIntervention.CreateCareInterventionResponse;
import com.eldercare.modules.careplan_management.careplan_design.repository.v2.CarePlanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v2/care-plans")
public class CarePlanContentController {
    private final CarePlanRepository plans;
    public CarePlanContentController(CarePlanRepository plans) { this.plans = plans; }

    public record GoalContent(CreateCareGoalResponse goal, List<CreateCareInterventionResponse> interventions) {}
    public record VersionContent(Long id, int versionNumber, String status, List<GoalContent> goals) {}
    public record PlanContent(Long id, String state, List<VersionContent> versions) {}

    @GetMapping("/{id}/content")
    @Transactional(readOnly = true)
    public ApiResponse<PlanContent> content(@PathVariable("id") Long id) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in to view care plans");
        }
        if (id <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid care plan id");
        var plan = plans.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Care plan not found"));
        var versions = plan.getVersions().stream()
                .sorted(Comparator.comparing(com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion::getId).reversed())
                .map(version -> new VersionContent(version.getId(), version.getVersionNumber(), version.getStatus(),
                        version.getGoals().stream().sorted(Comparator.comparing(com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareGoal::getId))
                                .map(goal -> new GoalContent(CreateCareGoalResponse.from(goal, dateOnly(goal.getTargetDate())),
                                        goal.getInterventions().stream()
                                                .sorted(Comparator.comparingInt(com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareIntervention::getSortOrder)
                                                        .thenComparing(com.eldercare.modules.careplan_management.careplan_design.entity.v2.CareIntervention::getId))
                                                .map(CreateCareInterventionResponse::from).toList())).toList())).toList();
        return ApiResponse.success(new PlanContent(plan.getId(), plan.getState(), versions));
    }

    private static LocalDate dateOnly(Date date) {
        if (date == null) return null;
        if (date instanceof java.sql.Date sqlDate) return sqlDate.toLocalDate();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ApiResponse.error(ex.getStatusCode().value(), ex.getReason()));
    }
}
