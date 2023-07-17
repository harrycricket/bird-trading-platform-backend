package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class ShopOwnerController {
    private final ShopOwnerService shopOwnerService;
    private final AccountService accountService;

    @PostMapping("/shop-owner")
    public ResponseEntity<?> registerShopOwnerAccount(
            @RequestParam(name = "image") MultipartFile multipartImage,
            @RequestPart(name = "data") RegisterShopOwnerDto registerShopOwnerDto) {

        return accountService.registerShopOwnerAccount(registerShopOwnerDto, multipartImage);
    }

    @GetMapping("/shop-owner/line-chart")
    public List<LineChartDto> getListLineChartDto(@RequestParam(required = false) String date) {
        return shopOwnerService.getDataLineChart(date, 7);
    }

    @GetMapping("/shop-owner/pie-chart")
    public List<PieChartDto> getListPieChartDto() {
        List<PieChartDto> dataPieChart = shopOwnerService.getDataPieChart();
        for (PieChartDto pie : dataPieChart) {
            log.info("pie {}", pie);
        }
        return dataPieChart;
    }

    @GetMapping("/shop-owner/bar-chart/price")
    public DataBarChartDto getListBarChartPriceDto() {
        return shopOwnerService.dataBarChartByPriceAllTypeProduct();
    }

    @GetMapping("/shop-owner/bar-chart/order")
    public DataBarChartDto getListBarChartOrderDto() {
        return shopOwnerService.dataBarChartByOrderAllTypeProduct();
    }

    @GetMapping("/shop-owner/bar-chart/review")
    public DataBarChartDto getListBarChartReviewDto() {
        return shopOwnerService.dataBarChartByReviewAllTypeProduct();
    }

    @GetMapping("/shop-owner/redirect")
    public ResponseEntity<?> redirectToShopOwner() {
        return shopOwnerService.redirectToShopOwner();
    }

    @GetMapping("/shop-owner/profile")
    public ResponseEntity<?> getShopInfoById() {
        return shopOwnerService.getShopInfoByUserId();
    }

    @PostMapping("/shop-owner/create-staff")
    public ResponseEntity<?> createAccountStaff(@RequestBody CreateAccountSaffDto createAccountSaffDto) {
        return shopOwnerService.createAccountStaff(createAccountSaffDto);
    }

    @GetMapping("/shop-owner/staffs/pages/{pageNumber}")
    public ResponseEntity<?> getShopStaff(@PathVariable("pageNumber") int pageNumber) {
        return shopOwnerService.getShopStaff(pageNumber);
    }

    @PutMapping("/shop-owner/profile")
    public ResponseEntity<?> updateShopOwnerProfile(@RequestParam(name = "avatar", required = false) MultipartFile avatarImg,
                                                    @RequestParam(name = "cover", required = false) MultipartFile coverImg,
                                                    @RequestPart(name = "data") ShopOwnerUpdateDto shopOwnerDto) {
        return shopOwnerService.updateShopOwnerProfile(avatarImg, coverImg, shopOwnerDto);
    }

    @GetMapping("/admin/shop-owner-account")
    public ResponseEntity<?> getAllShopOwner(@RequestParam String data) {
        try {
            return shopOwnerService.filterAllShopOwner(JsonUtil.INSTANCE.getObject(data, ShopOwnerAccountFilterDto.class));
        } catch (Exception e) {
            return ResponseUtils.getErrorResponseBadRequest("Data parse not correct.");
        }
    }

    @PutMapping("/admin/shop-owner/status")
    public ResponseEntity<?> updateListShopOwnerAccountStatus(@RequestBody ChangeStatusListIdDto changeStatusListIdDto) {
        return shopOwnerService.updateListShopOwnerAccountStatus(changeStatusListIdDto);
    }
}