package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldProductTable {
    ID("id"),
    NAME("name"),
    TYPE("type"),
    PRICE("price"),
    DISCOUNTED_PRICE("discountedPrice"),
    STATUS("status");
    private String field;
}
