package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.enums.TransactionStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PackageOrderAdminDto {
    private Long id;
    private Long accountId;
    private PaymentMethod paymentMethod;
    private String payerEmail;
    private TransactionStatus transactionStatus;
    private double totalPayment;
    private Long createdDateTransaction;
    private Long lastedUpdateTransaction;
    private String statusOrders;
    private String fullName;
    private String phoneNumber;
    private String address;
}
