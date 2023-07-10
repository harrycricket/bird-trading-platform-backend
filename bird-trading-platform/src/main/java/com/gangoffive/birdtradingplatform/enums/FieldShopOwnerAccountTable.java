package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldShopOwnerAccountTable {
    ID("id"),
    EMAIL("email"),
    SHOP_NAME("shopName"),
    SHOP_PHONE("shopPhone"),
    ADDRESS("address"),
    STATUS("status"),
    CREATED_DATE("createdDate");
    private String field;
}
