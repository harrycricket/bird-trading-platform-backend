package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccountService {
    Account updateAccount(AccountUpdateDto accountUpdateDto);

    ResponseEntity<?> verifyToken(String token, boolean isResetPassword);

    long retrieveShopID(long receiveId);
    public List<Long> getAllChanelByUserId (long userId);
}
