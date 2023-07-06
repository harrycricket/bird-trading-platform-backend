package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderDetailDto {
    private Long productId;
    private String productName;
    private String imgUrl;
    private int quantity;
    private double price;
    private double productPromotionRate;
    private List<PromotionShopDto> listPromotion;
}
