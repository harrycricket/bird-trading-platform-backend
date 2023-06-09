package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
public class PackageOrderServiceTest {
    @Autowired
    private PackageOrderService packageOrderService;

    @Test
    @Transactional
    public void checkPromotion() {
        boolean check = packageOrderService.checkPromotion(1L);
        assertTrue(check);
    }

    @Test
    @Transactional
    public void checkProduct() {
        Map<Long, Integer> productOrder = new HashMap<>();
        productOrder.put(1L, 1);
        productOrder.put(2L, 1);
        boolean check = packageOrderService.checkListProduct(productOrder);
        assertTrue(check);
    }

    @Test
    @Transactional
    public void checkUserOrder() {
        UserOrderDto userOrderDto = new UserOrderDto("1", "null", "1", "1", "1", "1", "1");
        boolean check = packageOrderService.checkUserOrderDto(userOrderDto);
        assertTrue(check);
    }

}
