package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.DataBarChartDto;
import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.dto.ProductShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
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
    private final ProductService productService;
    private final ShopOwnerService shopOwnerService;
    private final AccountRepository accountRepository;
    private final TagService tagService;

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
    public DataBarChartDto getListBarChartPriceDto() {
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
    public DataBarChartDto getListBarChartOrderDto() {
        return shopOwnerService.dataBarChartByOrderAllTypeProduct("YamamotoEmi37415@gmail.com");
    }

    @GetMapping("/bar-chart/review")
    public DataBarChartDto getListBarChartReviewDto() {
        return shopOwnerService.dataBarChartByReviewAllTypeProduct("YamamotoEmi37415@gmail.com");
    }

    @GetMapping("/tags")
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @PostMapping("/tag")
    public ResponseEntity<?> addNewTag(@RequestParam String name) {
        return tagService.addNewTag(name);
    }

    @PostMapping("/products")
    public ResponseEntity<?> addNewProduct(
            @RequestParam("image") List<MultipartFile> multipartFiles,
            @RequestParam("video") MultipartFile multipartVideo,
//            @RequestParam("data") MultipartFile data,
            @RequestPart("data") ProductShopOwnerDto productShopOwnerDto
    ) {
        log.info("productShopOwnerDto {}", productShopOwnerDto);
        return productService.addNewProduct(multipartFiles, multipartVideo, productShopOwnerDto);
    }

    @GetMapping("/redirect")
    public void redirectToShopOwner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String token = shopOwnerService.redirectToShopOwner(username);
        response.sendRedirect("https://admin.birdland2nd.store/get-token?token=" + token);
    }

    @GetMapping("/redirect/local")
    public void redirectLocalToShopOwner(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        String token = shopOwnerService.redirectToShopOwner(username);
        response.sendRedirect("http://localhost:3001/get-token?token=" + token);
    }
}