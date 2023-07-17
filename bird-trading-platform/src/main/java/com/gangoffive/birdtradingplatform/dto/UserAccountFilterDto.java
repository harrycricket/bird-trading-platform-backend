package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserAccountFilterDto {
    private SearchInfoDto userSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
