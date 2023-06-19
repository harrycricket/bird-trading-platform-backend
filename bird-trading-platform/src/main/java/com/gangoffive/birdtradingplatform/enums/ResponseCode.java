package com.gangoffive.birdtradingplatform.enums;

public enum ResponseCode {
    //Common
    SUCCESS(0, "Success"),
    FAILED(1, "Failed"),

    //Auth
    REGISTER_USERNAME_OR_PASSWORD(10, "Invalid username or Password"),
    AUTHENTICATION_FAILED(11, "Invalid refresh token"),

    //Product
    NOT_FOUND_THIS_ID(20,"Cannot found this product!"),
    NOT_FOUND_THIS_LIST_ID(21, "Cannot found this product with this list id!"),

    //Shop owner
    NOT_FOUND_LIST_SHOP_BY_LIST_ID(31, "Cannot found shop with that list shop id"),

    NOT_FOUND_THIS_PRODUCT_SHOP_ID(22, "Cannot found this product with shop id!"),
    //Promotion shop
    NOT_FOUND_THIS_SHOP_ID(31, "Cannot found promotion shop with this id!");

    private final int code;
    private final String message;

    ResponseCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ResponseCode{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
