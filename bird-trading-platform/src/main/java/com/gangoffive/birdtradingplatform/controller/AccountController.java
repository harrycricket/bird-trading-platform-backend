package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;


    @PutMapping("/users/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam("image") MultipartFile multipartImage,
            @RequestPart(name = "data") AccountUpdateDto accountUpdateDto) {
        return accountService.updateAccount(accountUpdateDto, multipartImage);
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
