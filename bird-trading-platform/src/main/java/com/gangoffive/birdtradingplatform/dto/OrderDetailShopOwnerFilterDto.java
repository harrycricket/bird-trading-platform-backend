package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderDetailShopOwnerFilterDto {
    private ShopOwnerSearchInfoDto orderSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
