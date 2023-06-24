package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class CartDto {
    private List<ItemByShopDto> itemsByShop;
    private List<Long> promotionIds;
    private PaymentMethod paymentMethod;
    private TotalOrderDto total;
}
