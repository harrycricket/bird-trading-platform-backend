package com.gangoffive.birdtradingplatform.enums;

public enum ProductStatus {
    DELETE(-1, "Delete"),
    INACTIVE(0, "Inactive"),
    ACTIVE(1, "Active"),
    BAN(2, "Ban by admin");
    private int statusCode;
    private String decription;

    ProductStatus(int statusCode, String decription) {
        this.statusCode = statusCode;
        this.decription = decription;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDecription() {
        return decription;
    }
}
