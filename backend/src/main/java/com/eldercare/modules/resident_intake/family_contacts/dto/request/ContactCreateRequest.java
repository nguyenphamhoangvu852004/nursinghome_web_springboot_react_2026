package com.eldercare.modules.resident_intake.family_contacts.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactCreateRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 100, message = "First name must not exceed 100 characters")
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    private String lastName;

    @NotBlank(message = "Primary phone is required")
    @Pattern(regexp = "^1-\\d{3}-\\d{3}-\\d{4}$", message = "Primary phone must follow format 1-XXX-XXX-XXXX")
    private String phonePrimary;

    @Pattern(regexp = "^$|^1-\\d{3}-\\d{3}-\\d{4}$", message = "Secondary phone must follow format 1-XXX-XXX-XXXX")
    private String phoneSecondary;

    @Email(message = "Email format is invalid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    private Long addressId;
}