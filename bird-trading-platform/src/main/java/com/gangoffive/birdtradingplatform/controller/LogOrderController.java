package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.LogOrderFilterDto;
import com.gangoffive.birdtradingplatform.service.LogOrderService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
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
public class LogOrderController {
    private final LogOrderService logOrderService;

    @GetMapping("/shop-owner/log-orders")
    public ResponseEntity<?> getAllOrder(@RequestParam String data) {
        try {
            return logOrderService.filterAllLogOrder(JsonUtil.INSTANCE.getObject(data, LogOrderFilterDto.class));
        } catch (Exception e) {
            return ResponseUtils.getErrorResponseBadRequest("Data parse not correct.");
        }
    }
}
