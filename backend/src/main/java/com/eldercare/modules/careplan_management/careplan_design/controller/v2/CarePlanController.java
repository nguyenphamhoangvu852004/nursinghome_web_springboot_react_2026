package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.admin.user_management.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan.CreateCarePlanResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlanVersion.CreateCarePlanVersionRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlanVersion.CreateCarePlanVersionRespone;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.RejectCarePlanVersion.RejectCarePlanVersionRequest;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.SubmitWaitingForReview.SubmitWaitingForReviewRequest;
import com.eldercare.modules.careplan_management.careplan_design.entity.v2.CarePlanVersion;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CarePlanService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController(value = "CarePlanControllerV2")
@RequestMapping("/api/v2/care-plans")
public class CarePlanController implements CarePlanAPI {

    private final CarePlanService carePlanService;
    private final UserRepository userRepository;

    public CarePlanController(CarePlanService carePlanService, UserRepository userRepository) {
        this.carePlanService = carePlanService;
        this.userRepository = userRepository;
    }


    @Override
    @PostMapping("")
    public ApiResponse<CreateCarePlanResponse> createCarePlan(CreateCarePlanRequest carePlan) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in to create a care plan");
        }
        Long actorId = userRepository.findByEmailAndIsDeletedFalse(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User session not found"))
                .getId();
        if (carePlan == null || carePlan.residentId() == null || carePlan.residentId() <= 0
                || carePlan.admissionId() == null || carePlan.admissionId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Resident and admission are required");
        }
        CreateCarePlanRequest request = new CreateCarePlanRequest(
                carePlan.residentId(), carePlan.admissionId(), actorId, null, null, null, null);
        return ApiResponse.success(this.carePlanService.createNewCarePlan(request));
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleRequestError(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode())
                .body(ApiResponse.error(error.getStatusCode().value(), error.getReason()));
    }

    @Override
    public ApiResponse<CreateCarePlanVersionRespone> createNewVersion(CreateCarePlanVersionRequest version) {

//        return ApiResponse.success(this.carePlanService.createNewVersion(version));
        return null;
    }

    @Override
    @PutMapping("/version")
    public ApiResponse<CarePlanVersion> changeCarePlanVersionStatus(HashMap<String, String> body) {
        Long versionId = Long.valueOf(body.get("versionId"));
        String action = body.get("action");
        Long actorId = Long.valueOf(body.get("actorId"));
        String rejectReason = body.get("rejectReason");
        switch (action) {
            case "submit": {
                SubmitWaitingForReviewRequest request = new SubmitWaitingForReviewRequest(versionId, actorId);
                this.carePlanService.submitWaitingForReview(request);
                break;
            }
            case "approve": {
//                this.carePlanService.approveVersion();
                break;
            }
            case "reject": {
                RejectCarePlanVersionRequest request = new RejectCarePlanVersionRequest(versionId, actorId, rejectReason);
                this.carePlanService.rejectVersion(request);
                break;
            }
            default: {

                break;
            }
        }
        return ApiResponse.error(400, "Invalid body pls check again");
    }
}
