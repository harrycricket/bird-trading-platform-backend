package com.gangoffive.birdtradingplatform.controller;


import com.gangoffive.birdtradingplatform.service.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Slf4j
public class OrderDetailController {
    private final OrderDetailService orderDetailService;
    @GetMapping("shop-owner/order-detail")
    public ResponseEntity<?> getAllOrderDetail(@RequestParam int pageNumber) {
        if (pageNumber > 0) {
            pageNumber--;
        }
        return orderDetailService.getAllOrderByShopOwner(pageNumber);
    }
}
