package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PackageOrderDto {
    private Long id;
    private Date createdDate;
    private Date lastedUpdate;
    private PaymentMethod paymentMethod;
    private String address;
    private double totalPriceProduct;
    private double shippingFee;
    private double discount;
    private double totalPayment;
}
