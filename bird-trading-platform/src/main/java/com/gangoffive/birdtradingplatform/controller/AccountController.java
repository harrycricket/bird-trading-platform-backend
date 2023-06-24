package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.dto.RegisterShopOwnerDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class AccountController {

    private final AccountService accountService;
    private final ShopOwnerService shopOwnerService;

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam(name = "image", required = false) MultipartFile multipartImage,
            @RequestPart(name = "data") AccountUpdateDto accountUpdateDto) {
        return accountService.updateAccount(accountUpdateDto, multipartImage);
    }

    @GetMapping("/verify/register")
    public ResponseEntity<?> verifyAccountRegister(@RequestParam String token) {
        return accountService.verifyToken(token, false);
    }

    @GetMapping("/verify/reset-password")
    public ResponseEntity<?> verifyResetPassword(@RequestParam String token) {
        return accountService.verifyToken(token, true);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> verifyResetPassword(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "refreshToken");
        return ResponseEntity.ok("Cookie deleted");
    }

}
