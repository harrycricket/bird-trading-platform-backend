package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.dto.AuthenticationRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    String register(AccountDto accountDto);
    ResponseEntity<?> authenticate(AuthenticationRequestDto request);

    String resetPassword(String email);
}
