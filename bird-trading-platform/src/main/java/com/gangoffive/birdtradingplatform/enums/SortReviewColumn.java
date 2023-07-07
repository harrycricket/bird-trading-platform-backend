package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortReviewColumn {
    ID("id", "id"),
    ORDER_DETAIL_ID("orderDetailId", "orderDetail.id"),
    CUSTOMER_NAME("customerName", "account.fullName"),
    PRODUCT_NAME("productName", "orderDetail.product.name"),
    RATING("rating", "rating.star"),
    REVIEW_DATE("reviewDate", "reviewDate");

    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortReviewColumn.values())
                .filter(sortReviewColumn -> sortReviewColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortReviewColumn.values()).anyMatch(sortReviewColumn -> sortReviewColumn.getField().equals(field));
    }
}
