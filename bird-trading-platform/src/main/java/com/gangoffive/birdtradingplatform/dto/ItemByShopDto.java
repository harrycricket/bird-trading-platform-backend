package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Map;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ItemByShopDto {
    private Map<Long, Integer> listItems;
    private double distance;
    private double shippingFee;
    private Long shopId;
    private double totalShopPrice;
}
