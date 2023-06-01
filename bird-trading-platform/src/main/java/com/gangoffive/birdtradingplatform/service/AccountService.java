package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import org.springframework.http.ResponseEntity;

public interface AccountService {
    Account updateAccount(AccountUpdateDto accountUpdateDto);

    ResponseEntity<?> verifyToken(String token);
}
