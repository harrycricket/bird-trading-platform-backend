package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public enum TransactionStatus {
    PROCESSING,
    SUCCESS,
    REFUNDED;

    public static TransactionStatus getTransactionStatusByValue(int value) {
        List<TransactionStatus> listStatus = Arrays.asList(TransactionStatus.values());
        try {
            TransactionStatus result = listStatus.stream().filter(sta -> sta.ordinal() + 1 == value).findFirst().get();
            return result;
        } catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this value");
        }
    }
}
