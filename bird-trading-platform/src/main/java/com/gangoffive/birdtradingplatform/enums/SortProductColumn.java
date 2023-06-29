package com.gangoffive.birdtradingplatform.enums;

import lombok.*;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortProductColumn {
    ID("id", "id"),
    NAME("name", "name"),
    PRICE("price", "price"),
    DISCOUNTED_PRICE("discountedPrice", "productSummary.discountedPrice"),
    TYPE_BIRD("type", "typeBird.name"),
    TYPE_ACCESSORY("type", "typeAccessory.name"),
    TYPE_FOOD("type", "typeFood.name"),
    QUANTITY("quantity", "quantity"),
    TOTAL_ORDERS("totalOrders", "productSummary.totalQuantityOrder"),
    STAR("star", "productSummary.star"),
    TOTAL_REVIEWS("totalReviews", "productSummary.reviewTotal"),
    CREATE_DATE("createDate", "createdDate"),
    LAST_UPDATE("lastUpdate", "lastUpDated");

    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortProductColumn.values())
                .filter(sortProductColumn -> sortProductColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortProductColumn.values()).anyMatch(sortProductColumn -> sortProductColumn.getField().equals(field));
    }
}
