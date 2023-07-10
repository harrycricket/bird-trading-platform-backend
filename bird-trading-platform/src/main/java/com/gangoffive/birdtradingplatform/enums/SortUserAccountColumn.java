package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum SortUserAccountColumn {
    ID("id", "id"),
    EMAIL("email", "email"),
    FULL_NAME("fullName", "fullName"),
    PHONE_NUMBER("phoneNumber", "phoneNumber"),
    ADDRESS("address", "address.address"),
    CREATED_DATE("createdDate", "createdDate");
    private String field;
    private String column;

    public static String getColumnByField(String field) {
        return Arrays.stream(SortUserAccountColumn.values())
                .filter(sortUserAccountColumn -> sortUserAccountColumn.getField().equals(field))
                .findFirst()
                .get()
                .getColumn();
    }

    public static boolean checkField(String field) {
        return Arrays.stream(SortUserAccountColumn.values()).anyMatch(
                sortUserAccountColumn -> sortUserAccountColumn.getField().equals(field)
        );
    }
}
