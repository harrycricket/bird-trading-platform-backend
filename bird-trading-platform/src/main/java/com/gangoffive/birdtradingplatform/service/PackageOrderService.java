package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PackageOrderRequestDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PackageOrderService {
    ResponseEntity<?> packageOrder(PackageOrderRequestDto packageOrder, String paymentId, String payerId);
    void saveAll(PackageOrderRequestDto packageOrder, String paymentId, Account account, Map<Long, Integer> productOrder);
    boolean checkPromotion(PackageOrderRequestDto packageOrder, Map<Long, Integer> productOrder);
    boolean checkListProduct(Map<Long, Integer> productOrder);
    boolean checkUserOrderDto(UserOrderDto userOrder);
}
