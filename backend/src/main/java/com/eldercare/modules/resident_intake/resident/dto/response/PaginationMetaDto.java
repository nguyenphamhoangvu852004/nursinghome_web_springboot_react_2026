package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationMetaDto {
    private long total;
    private int page;
    private int pageSize;
}
