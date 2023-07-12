package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortPackageOrderAdminColumn {
    ID("id", "id"),
    ACCOUNT_ID("accountId", "account.id"),
    PAYER_EMAIL("payerEmail", "transaction.paypalEmail"),
    TOTAL_PAYMENT("totalPayment", "totalPrice"),
    CREATED_DATE_TRANSACTION("createdDateTransaction", "transaction.transactionDate"),
    LASTED_UPDATE_TRANSACTION("lastedUpdateTransaction", "transaction.lastedUpdate"),
    FULL_NAME("fullName", "shippingAddress.fullName"),
    PHONE_NUMBER("phoneNumber", "shippingAddress.phone"),
    ADDRESS("address", "shippingAddress.address");

    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortPackageOrderAdminColumn.values())
                .filter(sortPackageOrderAdminColumn -> sortPackageOrderAdminColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortPackageOrderAdminColumn.values())
                .anyMatch(sortPackageOrderAdminColumn -> sortPackageOrderAdminColumn.getField().equals(field));
    }
}
