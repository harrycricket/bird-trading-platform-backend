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

    @PutMapping("/users/updateprofile")
    public void updateProfile(@RequestBody AccountUpdateDto accountUpdateDto) {
        accountService.updateAccount(accountUpdateDto);
    }

    @GetMapping("/users/updateprofile")
    public String GetProfile() {
        return "GET";
    }

    @DeleteMapping("/users/updateprofile")
    public String DeleteProfile() {
        return "DELETE";
    }

    @GetMapping("/users/verify/register")
    public ResponseEntity<?> verifyAccountRegister(@RequestParam String token) {
        return accountService.verifyToken(token, false);
    }

    @GetMapping("/users/verify/resetpassword")
    public ResponseEntity<?> verifyResetPassword(@RequestParam String token) {
        return accountService.verifyToken(token, true);
    }
}
