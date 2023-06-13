package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.BarChartDto;
import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    @GetMapping("/products/{pagenumber}")
    public ResponseEntity retrieveAllProduct(@PathVariable int pagenumber) {
        return productService.retrieveProductByShopIdForSO(3, pagenumber);
    }
    @GetMapping("/line-chart")
    public List<LineChartDto> getListLineChartDto() throws ParseException {
//        return shopOwnerService.dataBumpChartByTypeProduct(accountRepository.findByEmail("YamamotoEmi37415@gmail.com").get(), Accessory.class);
//        String pattern = "MM-dd-yyyy";
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
//        simpleDateFormat.format(new Date("2023-06-14"));

        String sDate1 = "7/06/2023";
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
        return dataPieChart;
    }

    @GetMapping("/bar-chart/price")
    public List<BarChartDto> getListBarChartPriceDto() {
//        List<Order> allOrdersPreviousWeek = shopOwnerService.getAllOrdersPreviousWeek(accountRepository.findByEmail("YamamotoEmi37415@gmail.com").get());
//        for (Order order: allOrdersPreviousWeek) {
//            log.info("order id{}", order.getId());
//        }
//        for (LocalDate date : shopOwnerService.getAllDatePreviousWeek()) {
//            log.info("date id{}", date);
//        }
        return shopOwnerService.dataBarChartByPriceAllTypeProduct("YamamotoEmi37415@gmail.com");
    }

    @GetMapping("/bar-chart/order")
    public List<BarChartDto> getListBarChartOrderDto() {
        return shopOwnerService.dataBarChartByOrderAllTypeProduct("YamamotoEmi37415@gmail.com");
    }

    @GetMapping("/redirect")
    public void redirectToShopOwner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        StringBuffer url = request.getRequestURL();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String token = shopOwnerService.redirectToShopOwner(username);
        if (url.toString().startsWith("http://localhost:3000/")) {
            response.sendRedirect("http://localhost:3001/get-token?token=" + token);
        } else {
            response.sendRedirect("https://admin.birdland2nd.store/get-token?token=" + token);
        }

    }
}