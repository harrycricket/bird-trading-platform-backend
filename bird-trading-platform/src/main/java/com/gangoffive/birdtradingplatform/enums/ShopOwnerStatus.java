package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ShopOwnerStatus {
    ACTIVE(1),
    BAN(2);
    private int status;
    public static ShopOwnerStatus getAccountStatus (int status) {
        List<ShopOwnerStatus> listStatus = Arrays.asList(ShopOwnerStatus.values());
        try {
            ShopOwnerStatus result = listStatus.stream().filter(sta -> sta.getStatus() == status).findFirst().get();
            return result;
        }catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this status");
        }
    }
}
