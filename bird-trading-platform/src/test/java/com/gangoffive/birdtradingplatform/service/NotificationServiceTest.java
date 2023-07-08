package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
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

    public NotificationDto notiData() {
        NotificationDto noti = new NotificationDto();
        noti.setNotiText("Test notification");
        noti.setName("HERE IS TEST");
        noti.setRole(NotifiConstant.NOTI_USER_ROLE);
        return noti;
    }
    public Object[][] NotificationData() {
        return new Object[][] =
                {1, noti}
    }
}
