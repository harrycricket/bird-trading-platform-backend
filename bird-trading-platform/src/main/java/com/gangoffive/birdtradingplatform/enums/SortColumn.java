package com.gangoffive.birdtradingplatform.enums;

import lombok.*;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortColumn {
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
        return Arrays.stream(SortColumn.values())
                .filter(sortColumn -> sortColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortColumn.values()).anyMatch(sortColumn -> sortColumn.getField().equals(field));
    }
}
