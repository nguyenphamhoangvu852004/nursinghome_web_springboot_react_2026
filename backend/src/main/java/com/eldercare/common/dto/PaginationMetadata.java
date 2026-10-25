package com.eldercare.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginationMetadata {

    @JsonProperty("currentPage")
    private int currentPage;

    @JsonProperty("totalPage")
    private int totalPage;

    @JsonProperty("currentLimit")
    private int currentLimit;

    @JsonProperty("hasNext")
    private boolean hasNext;

    @JsonProperty("hasPrevious")
    private boolean hasPrevious;

    @JsonProperty("totalElements")
    private long totalElements;
}
