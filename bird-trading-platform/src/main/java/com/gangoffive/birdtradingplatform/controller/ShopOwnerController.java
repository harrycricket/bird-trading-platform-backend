package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/create-staff")
    public ResponseEntity<?> createAccountStaff(@RequestBody CreateAccountSaffDto createAccountSaffDto){
        return shopOwnerService.createAccountStaff(createAccountSaffDto);
    }
    @GetMapping("/staffs/pages/{pagenumber}")
    public ResponseEntity<?> getShopStaff(@PathVariable("pagenumber") int pageNumber){return shopOwnerService.getShopStaff(pageNumber);}

    @PutMapping
    public ResponseEntity<?> updateShopOwnerProfile(@RequestParam(name = "avatar", required = false) MultipartFile avatarImg,
                                                    @RequestParam(name ="cover", required = false) MultipartFile coverImg,
                                                    @RequestPart ShopInfoDto shopInfoDto) {
        return shopOwnerService.updateShopOwnerProfile(shopInfoDto);
    }
}