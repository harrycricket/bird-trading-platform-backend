package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortLogOrderColumn {
    ID("id", "id"),
    ORDER_ID("orderId", "order.id"),
    STATUS("status", "status"),
    TIMESTAMP("timestamp", "timestamp"),
    STAFF_ID("staffId", "shopStaff.id"),
    STAFF_USERNAME("staffUsername", "shopStaff.userName");
    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortLogOrderColumn.values())
                .filter(sortLogOrderColumn -> sortLogOrderColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortLogOrderColumn.values()).anyMatch(sortLogOrderColumn -> sortLogOrderColumn.getField().equals(field));
    }
}
