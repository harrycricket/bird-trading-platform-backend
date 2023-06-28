package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.dto.AuthenticationRequestDto;
import com.gangoffive.birdtradingplatform.dto.ResetPasswordDto;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    ResponseEntity<?> register(AccountDto accountDto);
    ResponseEntity<?> authenticate(AuthenticationRequestDto request, HttpServletResponse response);

    ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto);
}
