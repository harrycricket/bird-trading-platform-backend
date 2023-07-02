package com.gangoffive.birdtradingplatform.controller;

    import com.gangoffive.birdtradingplatform.common.KafkaConstant;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
    import com.gangoffive.birdtradingplatform.enums.UserRole;
    import com.gangoffive.birdtradingplatform.service.NotificationService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final NotificationService notificationService;

    @PostMapping("/noti/send")
    public ResponseEntity<?> sendNotification (@RequestBody NotificationDto notificationDto) {
        String notification = JsonUtil.INSTANCE.getJsonString(notificationDto);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(KafkaConstant.KAFKA_PRIVATE_NOTIFICATION, notification);
        try  {
            SendResult<String, String> response = future.get();
            log.info("Record metadata: {}", response.getRecordMetadata());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{userid}/notifications")
    public ResponseEntity<?> getNotification (@PathVariable long userid, @RequestParam int pagenumber) {
        return notificationService.getNotifications(userid, pagenumber, UserRole.USER);
    }

    @GetMapping("/users/{userid}/notifications/unread")
    public ResponseEntity<?> getUnreadNotification (@PathVariable long userid) {
        return notificationService.getUserUnreadNotification(userid, UserRole.USER);
    }

    @GetMapping("/shop-owner/{shopid}/notifications")
    public ResponseEntity<?> getNotificationShop (@PathVariable("shopid") long shopId, @RequestParam int pagenumber) {
        return notificationService.getNotifications(shopId, pagenumber, UserRole.SHOPOWNER);
    }

    @GetMapping("/shop-owner/{shopid}/notifications/unread")
    public ResponseEntity<?> getUnreadNotificationShop (@PathVariable("shopid") long shopId) {
        return notificationService.getUserUnreadNotification(shopId, UserRole.SHOPOWNER);
    }

}
