package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import static org.junit.jupiter.api.Assertions.*;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import com.gangoffive.birdtradingplatform.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
public class ProductServiceTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;
    @Autowired
    private ReviewRepository reviewRepository;
    @Test
    @Transactional
    public void TestCalculation() {
        var product =  productRepository.findById(1l).get();
        log.info("product {}",product.getId() );
        List<OrderDetail> orders = product.getOrderDetails();
//        orders.forEach(orderD -> {log.info("order id  {} ", orderD.getId());});
//        List<Long> listId = orders.stream().map(orderDetail -> orderDetail.getId()).collect(Collectors.toList());
//        List<Review> rvList = reviewRepository.findAllByOrderDetailIdIn(listId).get();
//        rvList.forEach(review -> {log.info("review {} {}", review.getRating(),review.getOrderDetail().getId());});
        log.info("calculation rating {}",productService.CalculationRating(orders)) ;
    }

    @Test
    @Transactional
    public void TestMapperCalculation(){
        var product =  productRepository.findById(1l).get();
    }

    @Test
    public void TestSaleOffPercent(){
//        List<Double> list = Arrays.asList(10.0,5.0,2.0);
//        double percent = productService.CalculateSaleOff(list, 100);
//        assertEquals(0.16,percent);
    }
}
