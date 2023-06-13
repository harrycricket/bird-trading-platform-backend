package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.BarChartDto;
import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.dto.RequestChartDto;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.List;

@RestController
@RequestMapping("api/v1/shop-owner")
@RequiredArgsConstructor
@Slf4j
public class ShopOwnerController {
    private final ShopOwnerService shopOwnerService;
    private final AccountRepository accountRepository;

    @PostMapping("/line-chart")
    public List<LineChartDto> getListLineChartDto(@RequestBody RequestChartDto requestChartDto) throws ParseException {
//        return shopOwnerService.dataBumpChartByTypeProduct(accountRepository.findByEmail("YamamotoEmi37415@gmail.com").get(), Accessory.class);
//        String pattern = "MM-dd-yyyy";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        simpleDateFormat.format(new Date("2023-06-14"));

//        String sDate1 = "14/06/2023";
//        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);

        return shopOwnerService.getDataLineChart(requestChartDto.getEmail(), requestChartDto.getDate());
//        List<PieChartDto> dataPieChart = shopOwnerService.getDataPieChart("YamamotoEmi37415@gmail.com");
//        for (PieChartDto pie : dataPieChart) {
//            log.info("pie {}", pie);
//        }
    }
    @PostMapping("/pie-chart")
    public List<PieChartDto> getListPieChartDto(@RequestBody RequestChartDto requestChartDto) {
        List<PieChartDto> dataPieChart = shopOwnerService.getDataPieChart(requestChartDto.getEmail());
        for (PieChartDto pie : dataPieChart) {
            log.info("pie {}", pie);
        }
        return  dataPieChart;
    }

    @PostMapping("/bar-chart/price")
    public List<BarChartDto> getListBarChartPriceDto(@RequestBody RequestChartDto requestChartDto) {
//        List<Order> allOrdersPreviousWeek = shopOwnerService.getAllOrdersPreviousWeek(accountRepository.findByEmail("YamamotoEmi37415@gmail.com").get());
//        for (Order order: allOrdersPreviousWeek) {
//            log.info("order id{}", order.getId());
//        }
//        for (LocalDate date : shopOwnerService.getAllDatePreviousWeek()) {
//            log.info("date id{}", date);
//        }
        return shopOwnerService.dataBarChartByPriceAllTypeProduct(requestChartDto.getEmail());
    }

    @PostMapping("/bar-chart/order")
    public List<BarChartDto> getListBarChartOrderDto(@RequestBody RequestChartDto requestChartDto) {
        return shopOwnerService.dataBarChartByOrderAllTypeProduct(requestChartDto.getEmail());
    }


}
