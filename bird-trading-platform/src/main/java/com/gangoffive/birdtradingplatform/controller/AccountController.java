package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @GetMapping("/users/logout")
    public ResponseEntity<?> verifyResetPassword(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request,response,"refreshToken");
        return ResponseEntity.ok("Cookie deleted");
    }
}
