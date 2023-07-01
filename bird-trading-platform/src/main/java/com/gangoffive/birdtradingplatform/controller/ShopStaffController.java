package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import com.gangoffive.birdtradingplatform.service.ShopStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/staffs")
@RequiredArgsConstructor
public class ShopStaffController {
    private final ShopStaffService shopStaffService;
    @PutMapping
    public ResponseEntity<?> updateStatusStaff (@RequestBody ChangeStatusListIdDto updateStatusDto) {
        return shopStaffService.updateStaus(updateStatusDto);
    }
}
