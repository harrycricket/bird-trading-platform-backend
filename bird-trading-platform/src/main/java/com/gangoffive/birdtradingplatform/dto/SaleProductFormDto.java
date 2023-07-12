package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class SaleProductFormDto {
    private double price;
    private int quantity;
    private List<PromotionShopDto> voucher;
}
