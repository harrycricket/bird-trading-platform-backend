package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AuthenticationShopStaffRequest;
import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import com.gangoffive.birdtradingplatform.service.ShopStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ShopStaffController {
    private final ShopStaffService shopStaffService;
    @PutMapping("/shop-owner/staffs")
    public ResponseEntity<?> updateStatusStaff (@RequestBody() ChangeStatusListIdDto updateStatusDto) {
        return shopStaffService.updateStatus(updateStatusDto);
    }

    @PostMapping("/staffs/authenticate")
    public ResponseEntity<?> authenticateShopStaff(@RequestBody AuthenticationShopStaffRequest request) {
        return shopStaffService.authenticateStaffAccount(request);
    }

    @GetMapping("/staff/test")
    public String test() {
        return "hello";
    }
}
