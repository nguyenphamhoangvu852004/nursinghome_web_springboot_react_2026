package com.eldercare.modules.resident_intake.family_contacts.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Meta {

    private long total;

    private int page;

    private int pageSize;

    private int totalPages;

    private boolean hasNext;

    private boolean hasPrevious;
}