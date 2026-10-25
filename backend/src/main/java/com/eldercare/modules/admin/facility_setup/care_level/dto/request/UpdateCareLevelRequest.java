package com.eldercare.modules.admin.facility_setup.care_level.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCareLevelRequest {

    @NotNull(message = "isDeleted is required")
    @JsonProperty("is_deleted")
    private Boolean isDeleted;

}

