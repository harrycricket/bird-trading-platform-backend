package com.gangoffive.birdtradingplatform.service;

import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<?> getAllOrderByPackageOrderId(Long packageOrderId);

    ResponseEntity<?> getAllOrderByShopOwner(int pageNumber);
}
