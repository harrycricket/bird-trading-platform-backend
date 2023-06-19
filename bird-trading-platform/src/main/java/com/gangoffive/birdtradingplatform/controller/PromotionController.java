package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotions")
public class PromotionController {
    private final PromotionService promotionService;
    @GetMapping
    public ResponseEntity<?> getAllPromotion() {
        return promotionService.getAllPromotion();
    }

}
