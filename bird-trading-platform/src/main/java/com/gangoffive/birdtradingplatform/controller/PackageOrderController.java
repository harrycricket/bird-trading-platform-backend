package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.PackageOrderDto;
import com.gangoffive.birdtradingplatform.dto.TransactionDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.service.PackageOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Slf4j
public class PackageOrderController {

    private final PackageOrderService packageOrderService;
    @RequestMapping(value = "/package-order", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getPackageOrder(
            @RequestBody PackageOrderDto packageOrderDto,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "PayerID", required = false) String payerId
    ) {
        packageOrderDto.getProductOrder().entrySet().forEach(pro -> log.info("pro {}", pro.getKey()));
        return packageOrderService.packageOrder(packageOrderDto, paymentId, payerId);
    }

    @RequestMapping("/view-order")
    public ResponseEntity<?> viewOrder() {
        return ResponseEntity.ok("");
    }
}
