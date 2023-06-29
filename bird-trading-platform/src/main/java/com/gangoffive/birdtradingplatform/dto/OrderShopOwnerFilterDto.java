package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OrderShopOwnerFilterDto {
    private ShopOwnerSearchInfoDto orderSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
