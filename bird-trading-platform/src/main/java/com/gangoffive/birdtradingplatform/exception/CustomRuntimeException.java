package com.gangoffive.birdtradingplatform.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomRuntimeException extends RuntimeException{
    private String errorCode;
    private String errorMessage;

    public CustomRuntimeException(String errorCode, String errorMessage) {
        super(String.format("Error with code: %s \n message: %s", errorCode, errorMessage));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
