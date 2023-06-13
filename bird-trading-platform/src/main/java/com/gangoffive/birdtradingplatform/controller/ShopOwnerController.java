package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import lombok.RequiredArgsConstructor;
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
public class ShopOwnerController {
    private final ShopOwnerService shopOwnerService;
    private final AccountRepository accountRepository;

    @GetMapping
    public List<LineChartDto> getListLineChartDto() throws ParseException {
//        return shopOwnerService.dataBumpChartByTypeProduct(accountRepository.findByEmail("YamamotoEmi37415@gmail.com").get(), Accessory.class);
//        String pattern = "MM-dd-yyyy";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        simpleDateFormat.format(new Date("2023-06-14"));
        String sDate1 = "14/06/2023";
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        return shopOwnerService.getTotalPriceAllOrderByEachDate("YamamotoEmi37415@gmail.com", date1);
    }
}
