package com.gangoffive.birdtradingplatform.util;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {
    public static ResponseEntity<?> getErrorResponseNotFoundOperator() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this operator.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
