package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ResidentListResponseDto {
    private Long id;
    private String name;
    private String room;
    private String status;
    private LocalDate dob;
    private Integer age;
    private String payerSource;
    private String referralSource;
}
