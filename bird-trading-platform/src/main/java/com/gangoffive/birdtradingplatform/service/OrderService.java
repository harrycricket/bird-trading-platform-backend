package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import com.gangoffive.birdtradingplatform.dto.OrderShopOwnerFilterDto;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<?> getAllOrderByPackageOrderId(Long packageOrderId);

    ResponseEntity<?> filterAllOrder(OrderShopOwnerFilterDto orderShopOwnerFilter, boolean isShopOwner, boolean isAdmin);

    ResponseEntity<?> updateStatusOfListOrder(ChangeStatusListIdDto changeStatusListIdDto);

    ResponseEntity<?> getAllOrderByShip(int pageNumber);

    ResponseEntity<?> updateStatusOrderOfShipping(ChangeStatusListIdDto changeStatusListIdDto, String token);

    ResponseEntity<?> getAllOrderDetailByOrderId(Long id);
}
