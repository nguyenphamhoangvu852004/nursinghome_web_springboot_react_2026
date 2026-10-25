package com.eldercare.modules.resident_intake.resident.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class ResidentListResponseContainerDto {
    private List<ResidentListResponseItemDto> residents;
    private PaginationMetaDto meta;
}
