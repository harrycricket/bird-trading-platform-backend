package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LogOrderFilterDto {
    private SearchInfoDto logOrderSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
