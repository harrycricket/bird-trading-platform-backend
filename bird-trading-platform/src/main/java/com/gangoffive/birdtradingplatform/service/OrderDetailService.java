package com.gangoffive.birdtradingplatform.service;

import org.springframework.http.ResponseEntity;

public interface OrderDetailService {
    ResponseEntity<?> getAllOrderByShopOwner(int pageNumber);
}
