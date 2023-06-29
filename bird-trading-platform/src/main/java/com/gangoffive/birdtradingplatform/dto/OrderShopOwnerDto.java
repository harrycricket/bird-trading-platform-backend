package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import lombok.*;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderShopOwnerDto {
    private Long id;
    private double totalPrice;
    private OrderStatus status;
    private double shippingFee;
    private PaymentMethod paymentMethod;
    private List<PromotionShopDto> promotionsShop;
    private Long createdDate;
    private Long lastedUpdate;
}
