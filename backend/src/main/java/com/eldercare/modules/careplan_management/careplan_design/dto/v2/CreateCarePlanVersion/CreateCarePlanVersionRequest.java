package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlanVersion;


import java.time.OffsetDateTime;

public record CreateCarePlanVersionRequest(
        Long residentId,
        Long carePlanId,
        Long basedOnVersionId,
        Long basedOnAssessmentId,
        Long createdFromReviewId,
        String changeSummary,
        String contentHash,
        OffsetDateTime effectiveFrom,
        OffsetDateTime effectiveTo,
        Long creatorId
) {
}
