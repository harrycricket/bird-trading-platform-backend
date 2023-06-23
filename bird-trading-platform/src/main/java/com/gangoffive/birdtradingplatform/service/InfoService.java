package com.gangoffive.birdtradingplatform.service;

import org.springframework.http.ResponseEntity;

public interface InfoService {
    ResponseEntity<?> getUserInfo(String token);

    ResponseEntity<?> getShopInfo(Long id);
}
