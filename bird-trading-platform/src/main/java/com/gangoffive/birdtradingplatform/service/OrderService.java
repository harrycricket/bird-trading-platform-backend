package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import org.springframework.http.ResponseEntity;

public interface OrderService {
    ResponseEntity<?> getAllOrderByPackageOrderId(Long packageOrderId);

    ResponseEntity<?> getAllOrderByShopOwner(int pageNumber);

    ResponseEntity<?> updateStatusOfListOrder(ChangeStatusListIdDto changeStatusListIdDto);
}
