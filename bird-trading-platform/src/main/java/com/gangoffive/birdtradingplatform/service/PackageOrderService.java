package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.PackageOrderRequestDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import com.gangoffive.birdtradingplatform.entity.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface PackageOrderService {
    ResponseEntity<?> packageOrder(PackageOrderRequestDto packageOrder, String paymentId, String payerId);

    boolean checkPromotion(PackageOrderRequestDto packageOrder, Map<Long, Integer> productOrder);

    boolean checkListProduct(Map<Long, Integer> productOrder);

    boolean checkUserOrderDto(UserOrderDto userOrder);

    ResponseEntity<?> viewAllPackageOrderByAccountId(int pageNumber);

}
