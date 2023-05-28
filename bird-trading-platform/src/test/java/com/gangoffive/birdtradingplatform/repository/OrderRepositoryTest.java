package com.gangoffive.birdtradingplatform.repository;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
public class OrderRepositoryTest {
    @Autowired
    private OrderDetailRepository orderDetailRepository;

    @Test
    public void testTotalQuantity(){
<<<<<<< HEAD
        Optional<Integer> temp = orderDetailRepository.findTotalQuantityByPId(1L);
=======
        int temp = orderDetailRepository.findTotalQuantityByPId(1L).get();
>>>>>>> origin/thong
//        log.info("here is temp {}", temp);
        assertEquals(8, temp.get());
    }
}
