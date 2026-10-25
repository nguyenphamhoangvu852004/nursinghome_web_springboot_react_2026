package com.eldercare.modules.admin.facility_setup.care_level.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CareLevelResponse {

    private Long id;

    @JsonProperty("level_code")
    private String levelCode;

    @JsonProperty("level_name")
    private String levelName;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;

}

