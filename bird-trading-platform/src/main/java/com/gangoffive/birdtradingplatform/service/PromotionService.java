package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PromotionService {
    ResponseEntity<?> getAllPromotion();
}
