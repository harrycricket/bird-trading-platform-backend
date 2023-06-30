package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PromotionShopOrderDto {
    private long id;
    private String name;
    private String description;
    private int discountRate;
    private int quantity;
    private long startDate;
    private long endDate;
}
