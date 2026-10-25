package com.eldercare.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PagedResponse<T> extends ApiResponse<T> {
    private PaginationMetadata metadata;

    public static <T> PagedResponse<T> of(T data, int statusCode, String message,
                                          int currentPage, int totalPage,
                                          int currentLimit, long totalElements) {
        PaginationMetadata meta = PaginationMetadata.builder()
                .currentPage(currentPage + 1)  // convert 0-based to 1-based for client
                .totalPage(totalPage)
                .currentLimit(currentLimit)
                .hasNext(currentPage + 1 < totalPage)
                .hasPrevious(currentPage > 0)
                .totalElements(totalElements)
                .build();

        PagedResponse<T> response = new PagedResponse<>();
        response.setStatusCode(statusCode);
        response.setMessage(message);
        response.setData(data);
        response.setMetadata(meta);
        return response;
    }
}
