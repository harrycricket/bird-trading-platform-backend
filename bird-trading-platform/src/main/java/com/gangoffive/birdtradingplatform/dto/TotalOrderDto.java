package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class TotalOrderDto {
    private double subTotal;
    private double shippingTotal;
    private double promotionFee;
    private double paymentTotal;
}
