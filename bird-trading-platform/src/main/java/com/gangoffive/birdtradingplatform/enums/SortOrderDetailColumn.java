package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortOrderDetailColumn {
    ORDER_DETAIL_ID("id", "id"),
    ORDER_ID("orderId", "order.id"),
    PRODUCT_ID("productId", "product.id"),
    PRODUCT_NAME("name", "product.name"),
    PRICE("price", "price"),
    QUANTITY("quantity", "quantity"),
    PROMOTION_RATE("promotionRate", "productPromotionRate"),
    CREATE_DATE("createdDate", "order.createdDate"),
    REVIEW_RATING("reviewRating", "review.rating.star");

    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortOrderDetailColumn.values())
                .filter(sortOrderDetailColumn -> sortOrderDetailColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortOrderDetailColumn.values())
                .anyMatch(sortOrderDetailColumn -> sortOrderDetailColumn.getField().equals(field));
    }
}
