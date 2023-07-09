package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderShopOwnerDto {
    private Long id;
    private Long shopId;
    private double totalPrice;
    private OrderStatusDto orderStatus;
    private double shippingFee;
    private PaymentMethod paymentMethod;
    private List<PromotionShopOrderDto> promotionsShop;
    private Long createdDate;
    private Long lastedUpdate;
}
