package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerFilterDto;
import org.springframework.http.ResponseEntity;

public interface OrderDetailService {
    ResponseEntity<?> getAllOrderByShopOwner(OrderDetailShopOwnerFilterDto orderDetailShopOwnerFilter);
}
