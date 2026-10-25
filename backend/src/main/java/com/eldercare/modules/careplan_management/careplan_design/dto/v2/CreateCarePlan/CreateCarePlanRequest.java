package com.eldercare.modules.careplan_management.careplan_design.dto.v2.CreateCarePlan;

import jakarta.annotation.Nullable;

import java.time.OffsetDateTime;

public record CreateCarePlanRequest(
        Long residentId,
        Long admissionId,
        Long creatorId,
        @Nullable OffsetDateTime openedAt,
        @Nullable OffsetDateTime closedAt,
        @Nullable OffsetDateTime closedReason,
//        @Nullable String state,
        @Nullable Long currentVersionId

) {
}
