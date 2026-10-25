package com.eldercare.modules.resident_intake.family_contacts.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResidentQuickContactResponse {

    private Long id;

    private String firstName;

    private boolean isPrimary;

    private boolean isGuarantor;
}
