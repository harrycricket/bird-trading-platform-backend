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
    ACTIVE(1, "Your shop is unbanned. Have a great day!"),
    BAN(2, "Your shop is banned. Please contact us through email at birdlan2nd.admin@gmail.com.");
    private int status;
    private String contentNotification;
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
