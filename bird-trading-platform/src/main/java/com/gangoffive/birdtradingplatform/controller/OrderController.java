package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import com.gangoffive.birdtradingplatform.dto.OrderShopOwnerFilterDto;
import com.gangoffive.birdtradingplatform.service.OrderService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @GetMapping("orders")
    public ResponseEntity<?> getAllOrderByPackageOrderId(@RequestParam Long packageOrderId) {
        return orderService.getAllOrderByPackageOrderId(packageOrderId);
    }

    @GetMapping("shop-owner/orders")
    public ResponseEntity<?> getAllOrderByShopOwner(@RequestParam String data) {
        return orderService.getAllOrderByShopOwner(JsonUtil.INSTANCE.getObject(data, OrderShopOwnerFilterDto.class));
    }

    @PutMapping("shop-owner/orders")
    public ResponseEntity<?> updateStatusOfListOrder(@RequestBody ChangeStatusListIdDto changeStatusListIdDto) {
        return orderService.updateStatusOfListOrder(changeStatusListIdDto);
    }

    @GetMapping("ship/orders")
    public ResponseEntity<?> getAllOrderByShipper(@RequestParam int pageNumber) {
        return orderService.getAllOrderByShip(pageNumber);
    }

    @PutMapping("ship/orders")
    public ResponseEntity<?> updateStatusOfOrder(@RequestParam("token") String token,
                                                 @RequestBody ChangeStatusListIdDto changeStatusListIdDto) {
        return orderService.updateStatusOrderOfShipping(changeStatusListIdDto, token);
    }

    @GetMapping("shop-owner/order-detail/order/{orderId}")
    public ResponseEntity<?> getAllOrderDetailByOrderId(@PathVariable Long orderId) {
        return orderService.getAllOrderDetailByOrderId(orderId);
    }
}
