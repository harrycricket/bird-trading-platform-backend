package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import com.gangoffive.birdtradingplatform.dto.UserAccountFilterDto;
import com.gangoffive.birdtradingplatform.dto.VerifyRequestDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.CookieUtils;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;
    private final ShopOwnerService shopOwnerService;

    @PutMapping("/users/update-profile")
    public ResponseEntity<?> updateProfile(
            @RequestParam(name = "image", required = false) MultipartFile multipartImage,
            @RequestPart(name = "data") AccountUpdateDto accountUpdateDto) {
        return accountService.updateAccount(accountUpdateDto, multipartImage);
    }

    @GetMapping("/users/verify/register")
    public ResponseEntity<?> verifyAccountRegister(VerifyRequestDto verifyRequest) {
        return accountService.verifyToken(verifyRequest, false);
    }

    @GetMapping("/users/verify/reset-password")
    public ResponseEntity<?> verifyResetPassword(VerifyRequestDto verifyRequest) {
        return accountService.verifyToken(verifyRequest, true);
    }

    @GetMapping("/users/logout")
    public ResponseEntity<?> verifyResetPassword(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "refreshToken");
        return ResponseEntity.ok("Cookie deleted");
    }

    @GetMapping("/admin/user-account")
    public ResponseEntity<?> getAllUser(@RequestParam String data) {
        return accountService.filterAllUserAccount(JsonUtil.INSTANCE.getObject(data, UserAccountFilterDto.class));
    }

    @PutMapping("/admin/accounts/status")
    public ResponseEntity<?> updateListUserAccountStatus(@RequestBody ChangeStatusListIdDto changeStatusListIdDto) {
        return accountService.updateListUserAccountStatus(changeStatusListIdDto);
    }
}
