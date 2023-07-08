package com.gangoffive.birdtradingplatform.service;


import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@Test
@Slf4j
public class PackageOrderServiceTest extends AbstractTestNGSpringContextTests {
    @Autowired
    private PackageOrderService packageOrderService;

    public PackageOrderServiceTest() {
    }

    @Test
    @Transactional
    public void checkProduct() {
        Map<Long, Integer> productOrder = new HashMap<>();
        productOrder.put(1L, 1);
        productOrder.put(2L, 1);
        boolean check = packageOrderService.checkListProduct(productOrder);
        Assert.assertTrue(check);
    }

    @Test
    @Transactional
    public void checkUserOrder() {
        UserOrderDto userOrderDto = new UserOrderDto("1", "null", "1");
        boolean check = packageOrderService.checkUserOrderDto(userOrderDto);
        Assert.assertTrue(check);
    }

}

