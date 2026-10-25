package com.eldercare.modules.careplan_management.careplan_design.controller.v2;

import com.eldercare.common.dto.PagedResponse;
import com.eldercare.common.response.ApiResponse;
import com.eldercare.modules.careplan_management.careplan_design.dto.v2.ListCarePlans.CarePlanListData;
import com.eldercare.modules.careplan_management.careplan_design.service.v2.CarePlanListQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v2/care-plans")
public class CarePlanQueryController {
    private final CarePlanListQueryService queryService;

    public CarePlanQueryController(CarePlanListQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public PagedResponse<CarePlanListData> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String versionStatus,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {
        return queryService.list(page, size, search, state, versionStatus, sortBy, sortDir);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidQuery(ResponseStatusException error) {
        return ResponseEntity.status(error.getStatusCode())
                .body(ApiResponse.error(error.getStatusCode().value(), error.getReason()));
    }
}
