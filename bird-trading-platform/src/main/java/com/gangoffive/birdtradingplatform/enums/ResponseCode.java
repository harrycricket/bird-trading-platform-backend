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
    THIS_ACCOUNT_NOT_HAVE_SHOP(32, "This account hasn't created shop."),
    NOT_FOUND_THIS_PRODUCT_SHOP_ID(33, "Cannot found this product with shop id!"),
    UPDATE_LIST_PRODUCT_STATUS_SUCCESS(34, "Update successfully"),

    UPDATE_LIST_PRODUCT_STATUS_FAIL(35, "Update Fail"),

    //Promotion shop
    NOT_FOUND_THIS_SHOP_ID(31, "Cannot found promotion shop with this id!"),

    //Notification
    NOT_FOUND_NOTIFICATION_ID(41, "Cannot found notification with this id!"),
    NOT_FOUND_UNREAD_NOTIFICATION(42, "Cannot get unread notification with this id!");
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
        return "{" +
                "code:" + code +
                ", message:'" + message + '\'' +
                '}';
    }
}
