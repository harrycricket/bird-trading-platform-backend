package com.gangoffive.birdtradingplatform.controller;


import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerFilterDto;
import com.gangoffive.birdtradingplatform.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Slf4j
public class OrderDetailController {
    private final OrderDetailService orderDetailService;

    @PostMapping("shop-owner/order-detail")
//    @GetMapping("shop-owner/order-detail")
    public ResponseEntity<?> getAllOrderDetail(@RequestBody OrderDetailShopOwnerFilterDto orderDetailFilter) {
        return orderDetailService.getAllOrderByShopOwner(orderDetailFilter);
    }
}
