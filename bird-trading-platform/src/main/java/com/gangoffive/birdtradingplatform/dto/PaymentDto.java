package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.enums.PaypalPaymentIntent;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PaymentDto {
    private double total;
    private String currency;
    private PaymentMethod method;
    private PaypalPaymentIntent intent;
    private String description;
    private String successUrl;
    private String cancelUrl;
}
