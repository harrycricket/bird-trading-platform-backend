package com.gangoffive.birdtradingplatform.controller;


import com.gangoffive.birdtradingplatform.dto.DataBumpChartDto;
import com.gangoffive.birdtradingplatform.dto.PaymentDto;
import com.gangoffive.birdtradingplatform.dto.PayoutDto;
import com.gangoffive.birdtradingplatform.service.AdminService;
import com.gangoffive.birdtradingplatform.service.PaypalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminService adminService;
    private final PaypalService paypalService;

    @GetMapping
    public List<DataBumpChartDto> getAllOrder() {
        return adminService.dataBumpChartRankOfShop();
    }

    @PostMapping("/payout")
    public void payout(@RequestBody PayoutDto payoutDto) {
        paypalService.createPayout(payoutDto);
    }
}
