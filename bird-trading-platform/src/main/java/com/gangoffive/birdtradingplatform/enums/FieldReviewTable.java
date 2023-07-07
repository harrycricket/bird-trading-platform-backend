package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldReviewTable {
    ID("id"),
    ORDER_DETAIL_ID("orderDetailId"),
    CUSTOMER_NAME("customerName"),
    PRODUCT_NAME("productName"),
    RATING("rating"),
    REVIEW_DATE("reviewDate");
    private String field;
}
