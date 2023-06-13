package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.service.PromotionShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/shop-owner")
@RequiredArgsConstructor
public class PromotionShopController {
    private final PromotionShopService promotionShopService;

    @GetMapping("/promotion-shop")
    public ResponseEntity<?> retrieveAllPromotion () {
        return promotionShopService.retrieveAllPromotionShop(3);
    }

}
