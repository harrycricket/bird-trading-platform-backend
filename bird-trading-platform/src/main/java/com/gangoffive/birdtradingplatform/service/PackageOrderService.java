package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PackageOrderDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PackageOrderService {
    ResponseEntity<?> packageOrder(PackageOrderDto packageOrderDto, String paymentId, String payerId);
    void saveAll(PackageOrderDto packageOrderDto);
    boolean checkPromotion(List<Long> promotionId);
    boolean checkListProduct(Map<Long, Integer> productOrder);
    boolean checkUserOrderDto(UserOrderDto userOrderDto);
}
