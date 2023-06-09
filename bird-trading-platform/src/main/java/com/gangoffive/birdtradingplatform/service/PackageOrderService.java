package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PackageOrderDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface PackageOrderService {
    ResponseEntity<?> packageOrder(PackageOrderDto packageOrderDto);

    boolean checkPromotion(Long promotionId);
    boolean checkListProduct(Map<Long, Integer> productOrder);
    boolean checkUserOrderDto(UserOrderDto userOrderDto);
}
