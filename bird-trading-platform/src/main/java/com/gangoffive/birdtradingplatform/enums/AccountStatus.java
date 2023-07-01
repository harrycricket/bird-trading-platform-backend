package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum AccountStatus {
    NOT_VERIFY(-1),
    VERIFY(1),
    BANNED(-2);
    private int status;
    public static AccountStatus getAccountStatus (int status) {
        List<AccountStatus> listStatus = Arrays.asList(AccountStatus.values());
        try {
            AccountStatus result = listStatus.stream().filter(sta -> sta.getStatus() == status).findFirst().get();
            return result;
        }catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this status");
        }
    }
}
