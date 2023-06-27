package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProductShopOwnerFilterDto {
    private int category;
    private ProductShopOwnerSearchInfoDto productSearchInfo;
    private SortDirectionDto sortDirection;
    private int pageNumber;
}
