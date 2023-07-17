package com.gangoffive.birdtradingplatform.util;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtils {
    public static ResponseEntity<?> getErrorResponseNotFound(String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage(message)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<?> getErrorResponseBadRequest(String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .errorMessage(message)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<?> getErrorResponseNotAcceptable(String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()))
                .errorMessage(message)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_ACCEPTABLE);
    }

    public static ResponseEntity<?> getErrorResponseLocked(String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.LOCKED.value()))
                .errorMessage(message)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.LOCKED);
    }

    public static ResponseEntity<?> getErrorResponseConflict(String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.CONFLICT.value()))
                .errorMessage(message)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    public static ResponseEntity<?> getErrorResponseUnauthorized(String message) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                .errorMessage(message)
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    public static ResponseEntity<?> getErrorResponseBadRequestPageNumber() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                .errorMessage("Page number cannot less than 1.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    public static ResponseEntity<?> getErrorResponseNotFoundOperator() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this operator.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<?> getErrorResponseNotFoundSortColumn() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this field in sort column.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    public static ResponseEntity<?> getErrorResponseNotFoundSortDirection() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this sort direction.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
