package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.entity.Food;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class CategoryTest {

    @Test
    public void testCategory() {
        log.info("id {} ", Category.getCategoryIdByName(new Bird().getClass().getSimpleName()));
        log.info("id {} ", Category.getCategoryIdByName(new Food().getClass().getSimpleName()));
        log.info("id {} ", Category.getCategoryIdByName(new Accessory().getClass().getSimpleName()));
    }
}
