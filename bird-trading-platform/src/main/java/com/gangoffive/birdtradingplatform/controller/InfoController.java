package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.service.InfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class InfoController {
    private final InfoService infoService;

    @GetMapping("info")
    public ResponseEntity<?> getUserInfo(@RequestParam String token) {
        return infoService.getUserInfo(token);
    }

    @GetMapping("shop-info")
    public ResponseEntity<?> getShopInfo(@RequestParam Long id) {
        return infoService.getShopInfo(id);
    }
}
