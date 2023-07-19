package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldLogOrderTable {
    ID("id"),
    ORDER_ID("orderId"),
    STATUS("status"),
    TIMESTAMP("timestamp"),
    STAFF_ID("staffId"),
    STAFF_USERNAME("staffUsername");
    private String field;
}
