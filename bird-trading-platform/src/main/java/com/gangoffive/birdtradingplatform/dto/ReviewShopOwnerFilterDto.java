package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ReviewShopOwnerFilterDto {
    private SearchInfoDto reviewSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
