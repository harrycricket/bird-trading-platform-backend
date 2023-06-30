package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import com.gangoffive.birdtradingplatform.service.PromotionShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shop-owner")
@RequiredArgsConstructor
public class PromotionShopController {
    private final PromotionShopService promotionShopService;

    @GetMapping("/promotion-shop")
    public ResponseEntity<?> retrieveAllPromotion () {
        return promotionShopService.retrieveAllPromotionShop();
    }
    @PostMapping("/promotion-shop")
    public ResponseEntity<?> createNewPromotionShop(@RequestBody PromotionShopDto promotionShop) {
        return promotionShopService.createNewPromotionShop(promotionShop);
    }
}
