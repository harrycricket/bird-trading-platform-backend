package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PackageOrderRequestDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PackageOrderService {
    ResponseEntity<?> packageOrder(PackageOrderRequestDto packageOrderRequestDto, String paymentId, String payerId);
    void saveAll(PackageOrderRequestDto packageOrderRequestDto);
    boolean checkPromotion(List<Long> promotionId);
    boolean checkListProduct(Map<Long, Integer> productOrder);
    boolean checkUserOrderDto(UserOrderDto userOrderDto);
}
