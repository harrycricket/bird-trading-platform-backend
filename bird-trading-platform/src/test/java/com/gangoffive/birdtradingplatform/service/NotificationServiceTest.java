package com.gangoffive.birdtradingplatform.service;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@SpringBootTest
@Test
@Slf4j
public class NotificationServiceTest {
    @Autowired
    private NotificationService notificationService;


}
