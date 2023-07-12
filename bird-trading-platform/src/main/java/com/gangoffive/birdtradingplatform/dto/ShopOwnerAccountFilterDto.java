package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShopOwnerAccountFilterDto {
    private SearchInfoDto shopOwnerSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
