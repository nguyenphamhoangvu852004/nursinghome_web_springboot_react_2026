package com.eldercare.modules.admin.user_management.users.dto.response;

import java.time.OffsetDateTime;

public record UserResponse(
    Long id,
    String fullName,
    String email,
    String phoneNumber,
    String roleName,
    String status,
    Boolean mfaEnabled,
    OffsetDateTime lastLoginAt
) {}