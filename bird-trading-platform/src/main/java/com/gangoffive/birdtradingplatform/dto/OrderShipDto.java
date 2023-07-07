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
public class OrderShipDto {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String address;
    private double totalPrice;
    private OrderStatusDto orderStatus;
    private double shippingFee;
    private PaymentMethod paymentMethod;
    private Long createdDate;
    private Long lastedUpdate;
}
