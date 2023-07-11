package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldUserAccountTable {
    ID("id"),
    EMAIL("email"),
    FULL_NAME("fullName"),
    PHONE_NUMBER("phoneNumber"),
    ADDRESS("address"),
    STATUS("status"),
    CREATED_DATE("createdDate");
    private String field;
}
