package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    @PutMapping("/users/update-profile")
    public void updateProfile(@RequestBody AccountUpdateDto accountUpdateDto) {
        accountService.updateAccount(accountUpdateDto);
    }

    @GetMapping("/users/verify/register")
    public ResponseEntity<?> verifyAccountRegister(@RequestParam String token) {
        return accountService.verifyToken(token, false);
    }

    @GetMapping("/users/verify/reset-password")
    public ResponseEntity<?> verifyResetPassword(@RequestParam String token) {
        return accountService.verifyToken(token, true);
    }
}
