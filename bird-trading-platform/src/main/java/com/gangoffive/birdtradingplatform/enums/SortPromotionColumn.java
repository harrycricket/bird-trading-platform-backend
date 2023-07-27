package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortPromotionColumn {
    ID("id", "id"),
    NAME("name", "name"),
    DESCRIPTION("description", "description"),
    DISCOUNT("discount", "discount"),
    MINIMUM_ORDER_VALUE("minimumOrderValue", "minimumOrderValue"),
    USAGE_LIMIT("usageLimit", "usageLimit"),
    USED("used", "used"),
    PROMOTION_TYPE("type", "type"),
    START_DATE("startDate", "startDate"),
    END_DATE("endDate","endDate");

    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortPromotionColumn.values())
                .filter(sortPromotionColumn -> sortPromotionColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortPromotionColumn.values()).anyMatch(sortPromotionColumn -> sortPromotionColumn.getField().equals(field));
    }
}
