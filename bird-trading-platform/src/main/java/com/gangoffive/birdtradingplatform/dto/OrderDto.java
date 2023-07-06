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
public class OrderDto {
    private OrderStatus orderStatus;
    private ShopOwnerDto shopOwner;
    private List<OrderDetailDto> orderDetails;
    private double totalPriceProduct;
    private double shippingFee;
    private PaymentMethod paymentMethod;
    private AddressInfoDto address;
    private Long createdDate;
    private Long lastedUpdate;
}
