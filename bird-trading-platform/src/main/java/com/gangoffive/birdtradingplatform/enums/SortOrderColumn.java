package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortOrderColumn {
    ID("id", "id"),
    PRICE("totalPrice", "totalPrice"),
    SHIPPING_FEE("shippingFee", "shippingFee"),
    CREATE_DATE("createdDate", "createdDate"),
    LAST_UPDATE("lastedUpdate", "lastedUpdate");

    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortOrderColumn.values())
                .filter(sortOrderColumn -> sortOrderColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortOrderColumn.values()).anyMatch(sortOrderColumn -> sortOrderColumn.getField().equals(field));
    }
}
