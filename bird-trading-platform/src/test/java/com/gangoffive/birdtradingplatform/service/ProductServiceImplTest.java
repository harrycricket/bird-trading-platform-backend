//package com.gangoffive.birdtradingplatform.service;
//
//import com.gangoffive.birdtradingplatform.entity.OrderDetail;
//import com.gangoffive.birdtradingplatform.repository.ProductRepository;
//import com.gangoffive.birdtradingplatform.repository.ReviewRepository;
//import com.gangoffive.birdtradingplatform.service.impl.ProductServiceImpl;
//import jakarta.transaction.Transactional;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@ExtendWith(SpringExtension.class)
//@Slf4j
//public class ProductServiceImplTest {
//    @Autowired
//    private ProductRepository productRepository;
//    @Autowired
//    private ProductService productService;
//    @Autowired
//    private ReviewRepository reviewRepository;
////    @Test
////    @Transactional
////    public void TestCalculation() {
////        var product =  productRepository.findById(1l).get();
//////        log.info("product {}",product.getId() );
////        List<OrderDetail> orders = product.getOrderDetails();
////        double result= productService.CalculationRating(orders);
//////        log.info("calculation rating {}",result) ;
////        assertEquals(3.3, result);
////    }
////
////    @Test
////    @Transactional
////    public void TestMapperCalculation(){
////        var product =  productRepository.findById(1l).get();
////    }
////
////    @Test
////    public void TestSaleOffPercent(){
////        List<Double> list = Arrays.asList(10.0,5.0,2.0);
////        double percent = productService.CalculateSaleOff(list, 100);
////        assertEquals(0.16,percent);
////    }
//
////    @Test
////    public void TestCalculateDiscountedPrice(){
////        double result = productService.CalculateDiscountedPrice(100, 0.145);
////        assertEquals(85.5,result);
////    }
////
////    @Test
////    public void TestCalculateDiscountedPriceInDatabase(){
//////        var product =  productRepository.findById(1l).get();
//////        var productDto = productService.
////        double result = productService.CalculateDiscountedPrice(100, 0.145);
////        assertEquals(85.5,result);
////    }
//
////    @Test
////    public void testCalulate () {
//////        int result = 1 + 1;
//////        assertEquals(2, result);
////    }
//}
