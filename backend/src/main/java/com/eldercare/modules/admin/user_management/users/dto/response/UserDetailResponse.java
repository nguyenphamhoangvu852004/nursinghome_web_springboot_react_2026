package com.eldercare.modules.admin.user_management.users.dto.response;

public record UserDetailResponse(
    Long id,
    String firstName,
    String middleName,
    String lastName,
    String email,
    String phoneNumber,
    Long roleId,
    String roleName,
    String status,
    Long facilityId,
    String facilityName
) {}