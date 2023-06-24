package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ShopSummaryDto {
    private ShopInfoDto shopInfoDto;
    private int totalProduct;
    private String rating;
    private int totalProductOrder;
}
