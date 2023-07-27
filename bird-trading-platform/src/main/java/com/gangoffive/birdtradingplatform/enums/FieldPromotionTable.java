package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldPromotionTable {
    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    DISCOUNT("discount"),
    MINIMUM_ORDER_VALUE("minimumOrderValue"),
    USAGE_LIMIT("usageLimit"),
    USED("used"),
    PROMOTION_TYPE("type"),
    START_DATE("startDate"),
    END_DATE("endDate");
    private String field;
}
