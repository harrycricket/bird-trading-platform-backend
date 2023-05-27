package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.BirdTradingPlatformApplication;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BirdTradingPlatformApplication.class)
@Slf4j
public class ProductSummaryServiceTest {
    @Autowired
    private ProductSummaryService productSummaryService;

    @Autowired
    ProductRepository productRepository;

    @Test
    @Transactional
    void TestUpdateReviewTotal(){
        var product = productRepository.findById(1l);
        int result = productSummaryService.updateReviewTotal(product.get());
        assertEquals(3, 3);
    }

    @Test
    @Transactional
    void TestUpdateStar() {
        var product = productRepository.findById(1l);
        var result = productSummaryService.updateProductStar(product.get());
        log.info("star {}", result);
    }

    @Test
    @Transactional
    void TestUpdateTotalQuantity() {
        var product = productRepository.findById(1l);
        var result = productSummaryService.updateTotalQuantityOrder(product.get());
        log.info("total quantity {}", result);
    }

}
