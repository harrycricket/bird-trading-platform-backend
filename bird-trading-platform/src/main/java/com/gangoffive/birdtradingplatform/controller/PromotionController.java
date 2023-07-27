package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.dto.PromotionFilterDto;
import com.gangoffive.birdtradingplatform.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PromotionController {
    private final PromotionService promotionService;

    @GetMapping("promotions")
    public ResponseEntity<?> getAllPromotion() {
        return promotionService.getAllPromotion();
    }

    @PostMapping("admin/promotion")
    public ResponseEntity<?> createPromotion(@RequestBody PromotionDto promotionDto) {
        return promotionService.createPromotion(promotionDto);
    }

//    @GetMapping("/admin/promotions")
//    public ResponseEntity<?> filterAllPromotions(@RequestParam String data) {
//        try {
//            return promotionService.filterAllPromotion(JsonUtil.INSTANCE.getObject(data, PromotionFilterDto.class));
//        } catch (Exception e) {
//            return ResponseUtils.getErrorResponseBadRequest("Data parse not correct.");
//        }
//    }

    @PostMapping("/promotions")
    public ResponseEntity<?> filterAllPromotions(@RequestBody PromotionFilterDto promotionFilter) {

        return promotionService.filterAllPromotion(promotionFilter);

    }
}
