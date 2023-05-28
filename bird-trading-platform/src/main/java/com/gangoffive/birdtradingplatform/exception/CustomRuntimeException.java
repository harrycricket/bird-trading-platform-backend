package com.gangoffive.birdtradingplatform.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomRuntimeException extends RuntimeException{
    private String errorCode;
    private String errorMessage;

}
