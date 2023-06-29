package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import com.gangoffive.birdtradingplatform.dto.OrderShopOwnerFilterDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.service.OrderService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Slf4j
public class OrderController {
    private final OrderService orderService;
    @GetMapping("orders")
    public ResponseEntity<?> getAllOrderByPackageOrderId(@RequestParam Long packageOrderId) {
        return null;
    }

    @GetMapping("shop-owner/orders")
    public ResponseEntity<?> getAllOrderByShopOwner(@RequestParam String data) {
        return orderService.getAllOrderByShopOwner(JsonUtil.INSTANCE.getObject(data, OrderShopOwnerFilterDto.class));
    }

    @PutMapping("shop-owner/orders")
    public ResponseEntity<?> updateStatusOfListOrder(@RequestBody ChangeStatusListIdDto changeStatusListIdDto) {
        return orderService.updateStatusOfListOrder(changeStatusListIdDto);
    }
}
