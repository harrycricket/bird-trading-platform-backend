package com.gangoffive.birdtradingplatform.enums;

public enum ResponseCode {
    //Common
    SUCCESS(0, "Success"),
    FAILED(1, "Failed"),

    //Auth
    REGITER_USERNAME_OR_PASSWORD(10, "Invalid username or Password"),
    AUTHENTICATION_FAILED(11, "Invalid refresh token");
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
}
