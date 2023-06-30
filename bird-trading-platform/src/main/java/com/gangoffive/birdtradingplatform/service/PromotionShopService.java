package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface PromotionShopService {
    ResponseEntity<?> retrieveAllPromotionShop();

    ResponseEntity<?> createNewPromotionShop(PromotionShopDto promotionShop);
}
