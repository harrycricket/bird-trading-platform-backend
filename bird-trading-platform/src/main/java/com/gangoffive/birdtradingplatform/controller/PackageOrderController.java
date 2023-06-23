package com.gangoffive.birdtradingplatform.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangoffive.birdtradingplatform.dto.PackageOrderRequestDto;
import com.gangoffive.birdtradingplatform.service.PackageOrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1")
@Slf4j
public class PackageOrderController {

    private final PackageOrderService packageOrderService;
    @RequestMapping(value = "/package-order", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<?> getPackageOrder(
            @RequestBody PackageOrderRequestDto packageOrderRequestDto,
            @RequestParam(value = "paymentId", required = false) String paymentId,
            @RequestParam(value = "PayerID", required = false) String payerId
    ) {
        packageOrderRequestDto.getProductOrder().entrySet().forEach(pro -> log.info("pro {}", pro.getKey()));
        return packageOrderService.packageOrder(packageOrderRequestDto, paymentId, payerId);
    }

    @RequestMapping("/view-all-package-order")
    public ResponseEntity<?> viewAllPackageOrder() {
        return ResponseEntity.ok("");
    }
}
