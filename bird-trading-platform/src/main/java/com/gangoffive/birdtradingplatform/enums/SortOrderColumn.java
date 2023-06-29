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
    PRICE("price", "totalPrice"),
    SHIPPING_FEE("shippingFee", "shippingFee"),
    QUANTITY("quantity", "quantity"),
    TOTAL_ORDERS("totalOrders", "productSummary.totalQuantityOrder"),
    STAR("star", "productSummary.star"),
    TOTAL_REVIEWS("totalReviews", "productSummary.reviewTotal"),
    CREATE_DATE("createDate", "createdDate"),
    LAST_UPDATE("lastUpdate", "lastUpDated");

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
