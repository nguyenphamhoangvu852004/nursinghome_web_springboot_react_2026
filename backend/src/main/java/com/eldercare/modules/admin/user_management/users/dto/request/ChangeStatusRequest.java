package com.eldercare.modules.admin.user_management.users.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ChangeStatusRequest(
    @NotBlank(message = "Status is required")
    @Pattern(regexp = "INVITED|ACTIVE|SUSPENDED|DEACTIVATED", message = "Invalid status value")
    String status
) {}