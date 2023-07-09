package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@SpringBootTest
@Test
@Slf4j
public class NotificationServiceTest  extends AbstractTestNGSpringContextTests {
    @Autowired
    private NotificationService notificationService;

    public NotificationServiceTest() {
    }

    public NotificationDto notiDataSuccess() {
        NotificationDto noti = new NotificationDto();
        noti.setNotiText("Test notification");
        noti.setName("HERE IS TEST");
        noti.setRole(NotifiConstant.NOTI_USER_ROLE);
        return noti;
    }

    public NotificationDto notiDataFail() {
        NotificationDto noti = new NotificationDto();
        noti.setNotiText("Test notification");
        noti.setName("HERE IS TEST");
        noti.setRole("ROLE ELSE");
        return noti;
    }

    public ResponseEntity<?> resultNotification() {
        JsonObject result = new JsonObject();
        result.addProperty("unread", 2);
        return ResponseEntity.ok(result.toString());
    }

    @DataProvider(name = "notificationData")
    public Object[][] notificationData() {
        return new Object[][] {
                // Test case 1
                { 1L, notiDataSuccess(), true },
                { 1L, notiDataSuccess(), true },
                // Test case 2
                { 1L, notiDataFail(), false },
                // Add more test cases as needed
        };
    }

    @Test(dataProvider = "notificationData")
    public void testPushNotificationForAUserID(Long userId, NotificationDto notificationDto, boolean expectedResult) {
        boolean result = notificationService.pushNotificationForAUserID(userId, notificationDto);
        Assert.assertEquals(result, expectedResult);
    }

}
