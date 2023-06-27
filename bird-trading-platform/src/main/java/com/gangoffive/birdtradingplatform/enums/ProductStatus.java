package com.gangoffive.birdtradingplatform.enums;

public enum ProductStatus {
    DELETE(-1, "Delete"),
    INACTIVE(0, "Inactive"),
    ACTIVE(1, "Active"),
    BAN(2, "Ban by admin");
    private int statusCode;
    private String description;

    ProductStatus(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }
}
