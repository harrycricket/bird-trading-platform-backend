package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.service.TagService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/v1/shop-owner")
@RequiredArgsConstructor
@Slf4j
public class ShopOwnerController {
    private final ShopOwnerService shopOwnerService;
    private final AccountService accountService;

    @PostMapping()
    public ResponseEntity<?> registerShopOwnerAccount(
            @RequestParam(name = "image") MultipartFile multipartImage,
            @RequestPart(name = "data") RegisterShopOwnerDto registerShopOwnerDto) {
        return accountService.registerShopOwnerAccount(registerShopOwnerDto, multipartImage);
    }

    @GetMapping("/line-chart")
    public List<LineChartDto> getListLineChartDto(@RequestParam(required = false) String date) {
        return shopOwnerService.getDataLineChart(date, 7);
    }

    @GetMapping("/pie-chart")
    public List<PieChartDto> getListPieChartDto() {
        List<PieChartDto> dataPieChart = shopOwnerService.getDataPieChart();
        for (PieChartDto pie : dataPieChart) {
            log.info("pie {}", pie);
        }
        return dataPieChart;
    }

    @GetMapping("/bar-chart/price")
    public DataBarChartDto getListBarChartPriceDto() {
        return shopOwnerService.dataBarChartByPriceAllTypeProduct();
    }

    @GetMapping("/bar-chart/order")
    public DataBarChartDto getListBarChartOrderDto() {
        return shopOwnerService.dataBarChartByOrderAllTypeProduct();
    }

    @GetMapping("/bar-chart/review")
    public DataBarChartDto getListBarChartReviewDto() {
        return shopOwnerService.dataBarChartByReviewAllTypeProduct();
    }

    @GetMapping("/redirect")
    public ResponseEntity<?> redirectToShopOwner() {
        return shopOwnerService.redirectToShopOwner();
    }

    @GetMapping("/profile")
    public ResponseEntity<?>  getShopInfoById () {
        return shopOwnerService.getShopInforByUserId ();
    }

}