package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PackageOrderAdminFilterDto {
    private SearchInfoDto packageOrderSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
