package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.OrderDetailDto;
import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerFilterDto;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import org.springframework.http.ResponseEntity;

public interface OrderDetailService {
    ResponseEntity<?> getAllOrderByShopOwner(OrderDetailShopOwnerFilterDto orderDetailShopOwnerFilter);

    OrderDetailDto orderDetailToOrderDetailDto(OrderDetail orderDetail);
}
