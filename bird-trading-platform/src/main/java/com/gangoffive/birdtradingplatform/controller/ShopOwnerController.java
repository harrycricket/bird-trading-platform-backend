package com.gangoffive.birdtradingplatform.controller;


import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/v1/shop-owner")
@RequiredArgsConstructor
@Slf4j
public class ShopOwnerController {
    private final ProductService productService;
    private final ShopOwnerService shopOwnerService;
    private final AccountRepository accountRepository;
    @GetMapping("/products")
    public ResponseEntity retrieveAllProduct() {
        return productService.retrieveProductByShopIdForSO(3, 1);
    }
    @GetMapping("/line-chart")
    public List<LineChartDto> getListLineChartDto() throws ParseException {
//        return shopOwnerService.dataBumpChartByTypeProduct(accountRepository.findByEmail("YamamotoEmi37415@gmail.com").get(), Accessory.class);
//        String pattern = "MM-dd-yyyy";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        simpleDateFormat.format(new Date("2023-06-14"));
        String sDate1 = "14/06/2023";
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        return shopOwnerService.getDataLineChart("YamamotoEmi37415@gmail.com", date1);
//        List<PieChartDto> dataPieChart = shopOwnerService.getDataPieChart("YamamotoEmi37415@gmail.com");
//        for (PieChartDto pie : dataPieChart) {
//            log.info("pie {}", pie);
//        }
    }
    @GetMapping("/pie-chart")
    public List<PieChartDto> getListPieChartDto() {
        List<PieChartDto> dataPieChart = shopOwnerService.getDataPieChart("YamamotoEmi37415@gmail.com");
        for (PieChartDto pie : dataPieChart) {
            log.info("pie {}", pie);
        }
        return  dataPieChart;
    }
}
