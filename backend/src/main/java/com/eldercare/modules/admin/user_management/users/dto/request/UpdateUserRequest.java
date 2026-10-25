package com.eldercare.modules.admin.user_management.users.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
    @NotBlank(message = "First name is required") String firstName,
    String middleName,
    @NotBlank(message = "Last name is required") String lastName,
    @NotBlank(message = "Email is required") @Email(message = "Email is invalid") String email,
    String phoneNumber,
    @NotNull(message = "Role is required") Long roleId,
    Long facilityId
) {}