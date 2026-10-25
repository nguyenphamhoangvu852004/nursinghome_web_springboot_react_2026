package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.exception.NotFoundException;
import com.eldercare.modules.admin.user_management.UserRepository;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCareGoal.*;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CareGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.sql.Date;

@RestController("CareGoalControllerV2")
@RequestMapping("/api/v2/care-plan-versions/{versionId}/goals")
public class CareGoalController {
    private final CareGoalService service;
    private final UserRepository users;

    public CareGoalController(CareGoalService service, UserRepository users) {
        this.service = service;
        this.users = users;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CreateCareGoalResponse>> createGoal(
            @PathVariable("versionId") Long versionId, @RequestBody CreateCareGoalBody body) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in to create a goal");
        }
        Long actorId = users.findByEmailAndIsDeletedFalse(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User session not found"))
                .getId();
        boolean canCreate = auth.getAuthorities().stream().anyMatch(authority ->
                "ROLE_NURSE".equalsIgnoreCase(authority.getAuthority())
                        || "ROLE_DON".equalsIgnoreCase(authority.getAuthority()));
     /*   if (!canCreate) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only Nurse or DON can create care goals");
        }*/
        if (versionId == null || versionId <= 0 || body == null) {
            throw new IllegalArgumentException("A positive version id and request body are required");
        }
        var request = new CreateCareGoalRequest(versionId, body.problemOrNeed(), body.title(),
                body.desiredOutcome(), body.baselineValue(), body.targetValue(), body.targetUnit(),
                body.measurementMethod(), body.targetDate() == null ? null : Date.valueOf(body.targetDate()),
                body.priority(), body.residentPreference(), actorId);
        var response = CreateCareGoalResponse.from(service.createGoal(request), body.targetDate());
        return ResponseEntity.created(URI.create("/api/v2/care-plan-versions/" + versionId + "/goals/" + response.id()))
                .body(ApiResponse.created("Care goal created", response));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.error(ex.getStatusCode().value(), ex.getReason()));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest().body(ApiResponse.error(400, "Invalid goal request: " + ex.getMessage()));
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
