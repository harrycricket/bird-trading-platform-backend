package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public enum PromotionType {
    DISCOUNT,
    SHIPPING;

    public static PromotionType getPromotionTypeBaseOnStatusCode(int statusCode) {
        List<PromotionType> listStatus = Arrays.asList(PromotionType.values());
        try {
            PromotionType result = listStatus.stream().filter(sta -> sta.ordinal() == statusCode).findFirst().get();
            return result;
        } catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this status");
        }
    }
}
