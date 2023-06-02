package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.security.oauth2.AuthenticationRequest;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    String register(AccountDto accountDto);
    ResponseEntity<?> authenticate(AuthenticationRequest request);

    String resetPassword(String email);
}
