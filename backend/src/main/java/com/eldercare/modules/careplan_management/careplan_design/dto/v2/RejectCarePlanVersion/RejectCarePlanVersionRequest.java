package com.eldercare.modules.careplan_management.careplan_design.dto.v2.RejectCarePlanVersion;

public record RejectCarePlanVersionRequest(
        Long versionId,
        Long actorId,
        String rejectReason
) {
}
