package com.eldercare.modules.careplan_management.careplan_design.dto.v2.ListCarePlans;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public record CarePlanListItem(
        long id, long residentId, String residentName, String roomNumber, String bedNumber,
        long admissionId, LocalDate admissionDate, String facilityName, String state,
        Long latestVersionId, Integer latestVersionNumber, String latestVersionStatus,
        Long effectiveVersionId, Integer effectiveVersionNumber,
        long goalCount, long interventionCount, String createdByName,
        OffsetDateTime openedAt, OffsetDateTime closedAt, String closedReason,
        OffsetDateTime createdAt, OffsetDateTime updatedAt
) { }
