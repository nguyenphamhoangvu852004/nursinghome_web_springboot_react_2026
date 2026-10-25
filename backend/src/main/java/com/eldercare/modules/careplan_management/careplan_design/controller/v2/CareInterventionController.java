package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareIntervention.*;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CareInterventionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import java.sql.Date;

@RestController("CareInterventionControllerV2")
@RequestMapping("/api/v2/care-goals/{goalId}/interventions")
public class CareInterventionController {
    private final CareInterventionService service;
    private final UserRepository users;

    public CareInterventionController(CareInterventionService service, UserRepository users) {
        this.service = service;
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateCareInterventionResponse>> createIntervention(
            @PathVariable("goalId") Long goalId, @RequestBody CreateCareInterventionBody body) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in to create an intervention");
        }
        Long actorId = users.findByEmailAndIsDeletedFalse(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User session not found"))
                .getId();
        boolean canCreate = auth.getAuthorities().stream().anyMatch(authority ->
                "ROLE_NURSE".equalsIgnoreCase(authority.getAuthority())
                        || "ROLE_DON".equalsIgnoreCase(authority.getAuthority()));
        if (!canCreate) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Nurse or DON can create interventions");
        }
        if (goalId == null || goalId <= 0 || body == null) {
            throw new IllegalArgumentException("A positive goal id and request body are required");
        }
        var request = new CreateCareInterventionRequest(goalId, body.title(), body.instructions(),
                body.responsibleDiscipline(), body.assignedRole(), body.frequencyCode(), body.scheduleDefinition(),
                body.startDate() == null ? null : Date.valueOf(body.startDate()),
                body.endDate() == null ? null : Date.valueOf(body.endDate()),
                body.prnCriteria(), body.evidenceOrRationale(), body.requiresTaskGeneration(), body.sortOrder(), actorId);
        var response = CreateCareInterventionResponse.from(service.createIntervention(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Care intervention created", response));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.error(ex.getStatusCode().value(), ex.getReason()));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "Invalid intervention request"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
        return ResponseEntity.status(404).body(ApiResponse.error(404, ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(IllegalStateException ex) {
        return ResponseEntity.status(409).body(ApiResponse.error(409, ex.getMessage()));
    }
}
