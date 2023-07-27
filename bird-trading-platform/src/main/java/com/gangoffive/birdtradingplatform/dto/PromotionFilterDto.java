package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PromotionFilterDto {
    private SearchInfoDto promotionSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
