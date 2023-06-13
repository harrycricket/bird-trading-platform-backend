package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PromotionShopDto {
    private long id;
    private String name;
    private String description;
    private int discountRate;
    private long startDate;
    private long endDate;
}
