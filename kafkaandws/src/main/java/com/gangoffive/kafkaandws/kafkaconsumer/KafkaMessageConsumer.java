package com.gangoffive.kafkaandws.kafkaconsumer;

import com.gangoffive.kafkaandws.constant.KafkaConstant;
import com.gangoffive.kafkaandws.constant.MessageConstant;
import com.gangoffive.kafkaandws.constant.NotifiConstant;
import com.gangoffive.kafkaandws.dto.MessageDto;
import com.gangoffive.kafkaandws.dto.NotificationDto;
import com.gangoffive.kafkaandws.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class KafkaMessageConsumer {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    private Logger log = LoggerFactory.getLogger(Logger.class);

    @KafkaListener(topics = KafkaConstant.KAFKA_PRIVATE_CHAT, groupId = KafkaConstant.KAFKA_GROUP_ID)
    public void consumeMessagePrivate(String message) {
        MessageDto messDto = JsonUtil.INSTANCE.getObject(message, MessageDto.class);
        try {
            this.sendMessage(messDto);

        }catch (Exception e) {
            log.info(e.getMessage());
        }
    }

//    @KafkaListener(topics = KafkaConstant.KAFKA_PUBLIC_NOTIFICATION, groupId = KafkaConstant.KAFKA_GROUP_ID)
    public void consumeNotificationPublic(String notification) {
        NotificationDto noti = JsonUtil.INSTANCE.getObject(notification, NotificationDto.class);

    }

    @KafkaListener(topics = KafkaConstant.KAFKA_PRIVATE_NOTIFICATION, groupId = KafkaConstant.KAFKA_GROUP_ID)
    public void consumeNotificationPrivate(String notification) {
        NotificationDto noti = JsonUtil.INSTANCE.getObject(notification, NotificationDto.class);
        this.sendNotificationPrivate(noti);
    }


    @Async
    void sendMessage(MessageDto message) {
        long userID = message.getUserID();
        long shopId = message.getShopID();
        long senderId = 0;
        long receiveId = 0;
        String destination = null;
        message.setId(System.currentTimeMillis());
        if(message.getSenderName().equalsIgnoreCase(MessageConstant.MESSAGE_SHOP_ROLE)) {
            receiveId = userID;
            //Set user id is id of account shop owner
            message.setUserID(-1);
            //check user cannot send to their shop
            destination = String.format("/chatroom/%d/user",receiveId);
        }else if (message.getSenderName().equalsIgnoreCase(MessageConstant.MESSAGE_USER_ROLE)) {
            receiveId = shopId;
            message.setShopID(-1);
            destination = String.format("/chatroom/%d/shop",receiveId);
        }
        log.info("detini {}", destination);
        log.info(String.format(MessageConstant.MESSAGE_SEND_LOG,receiveId, message.toString(),destination));
        //send to websocket
        messagingTemplate.convertAndSend(destination, message);
    }

    @Async
    void sendNotificationPublic (NotificationDto notification) {

    }

    @Async
    void sendNotificationPrivate (NotificationDto notification) {
        notification.setId(System.currentTimeMillis());
        String destination = null;
        destination = String.format("/notification/%d/user",notification.getReceiveId());
        if(notification.getRole().equalsIgnoreCase(NotifiConstant.NOTI_SHOP_ROLE)) {
            //take out the shop id base on account id
            destination = String.format("/notification/%d/shop", notification.getReceiveId());
        }else if (notification.getRole().equalsIgnoreCase(NotifiConstant.NOTI_USER_ROLE)) {
            destination = String.format("/notification/%d/user",notification.getReceiveId());
        }
        messagingTemplate.convertAndSend(destination, notification);
    }

}
