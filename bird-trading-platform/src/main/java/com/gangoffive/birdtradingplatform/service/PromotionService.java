package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PromotionService {
    ResponseEntity<?> getAllPromotion();
    ResponseEntity<?> createPromotion(PromotionDto createPromotion);
}

