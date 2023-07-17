package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldPackageOrderAdminTable {
    ID("id"),
    ACCOUNT_ID("accountId"),
    PAYMENT_METHOD("paymentMethod"),
    PAYER_EMAIL("payerEmail"),
    TRANSACTION_STATUS("transactionStatus"),
    TOTAL_PAYMENT("totalPayment"),
    CREATED_DATE_TRANSACTION("createdDateTransaction"),
    LASTED_UPDATE_TRANSACTION("lastedUpdateTransaction"),
    FULL_NAME("fullName"),
    PHONE_NUMBER("phoneNumber"),
    ADDRESS("address");
    private String field;
}
