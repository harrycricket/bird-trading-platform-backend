package com.gangoffive.birdtradingplatform.controller;


import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.service.*;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class AdminController {
    private final AdminService adminService;
    private final PaypalService paypalService;

    @GetMapping("admin/bump-chart")
    public List<DataBumpChartDto> getDataBumpChartRankOfShop() {
        return adminService.dataBumpChartRankOfShop();
    }

    @GetMapping("admin/pie-chart")
    public List<PieChartDto> getDataPieChartRankOfShop() {
        return adminService.dataPieChartRankOfShop();
    }

    @PostMapping("admin/payout")
    public void payout(@RequestBody PayoutDto payoutDto) {
        paypalService.createPayout(payoutDto);
    }

}
