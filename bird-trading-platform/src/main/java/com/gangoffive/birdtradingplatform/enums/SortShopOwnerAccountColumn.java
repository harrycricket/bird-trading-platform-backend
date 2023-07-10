package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortShopOwnerAccountColumn {
    ID("id", "id"),
    EMAIL("email", "email"),
    SHOP_NAME("shopName", "shopName"),
    SHOP_PHONE("shopPhone", "shopPhone"),
    ADDRESS("address", "address.address"),
    CREATED_DATE("createdDate", "createdDate");
    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortShopOwnerAccountColumn.values())
                .filter(shopOwnerAccountColumn -> shopOwnerAccountColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortShopOwnerAccountColumn.values()).anyMatch(
                shopOwnerAccountColumn -> shopOwnerAccountColumn.getField().equals(field)
        );
    }
}
