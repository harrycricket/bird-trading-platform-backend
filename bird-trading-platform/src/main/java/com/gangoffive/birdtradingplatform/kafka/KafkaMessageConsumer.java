package com.gangoffive.birdtradingplatform.kafka;

import com.gangoffive.birdtradingplatform.common.KafkaConstant;
import com.gangoffive.birdtradingplatform.common.MessageConstant;
import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.dto.MessageDto;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Channel;
import com.gangoffive.birdtradingplatform.entity.Message;
import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.MessageMapper;
import com.gangoffive.birdtradingplatform.mapper.NotificationMapper;
import com.gangoffive.birdtradingplatform.service.*;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;


@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaMessageConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ChannelService channelService;
    private final MessageMapper messageMapper;
    private final NotificationMapper notificationMapper;
    private final NotificationService notificationService;
    private final AccountService accountService;
    private final ShopOwnerService shopOwnerService;

//    @KafkaListener(topics = KafkaConstant.KAFKA_PRIVATE_CHAT, groupId = KafkaConstant.KAFKA_GROUP_ID)
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

//    @KafkaListener(topics = KafkaConstant.KAFKA_PRIVATE_NOTIFICATION, groupId = KafkaConstant.KAFKA_GROUP_ID)
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
            senderId = shopOwnerService.getAccountIdByShopid(shopId);
            //Set user id is id of account shop owner
            message.setUserID(senderId);
            //check user cannot send to their shop
            if(senderId == receiveId) {
                throw new CustomRuntimeException("400", "You cannot send message for your shop!");
            }
            destination = String.format("/chatroom/%d/user",receiveId);
        }else if (message.getSenderName().equalsIgnoreCase(MessageConstant.MESSAGE_USER_ROLE)) {
            senderId = userID;
            receiveId = shopId;
            long accountShopId = shopOwnerService.getAccountIdByShopid(shopId);
            //check user cannot send to their shop
            if(senderId == accountShopId) {
                throw new CustomRuntimeException("400", "You cannot send message for your shop!");
            }
            destination = String.format("/chatroom/%d/shop",receiveId);
        } else {
            throw new CustomRuntimeException("400","Sender name not correct!");
        }
        log.info("detini {}", destination);
        log.info(String.format(MessageConstant.MESSAGE_SEND_LOG,receiveId, message.getContent(),destination));
        //send to websocket
        messagingTemplate.convertAndSend(destination, message);
        //save to database
        Channel channel = channelService.getAndSaveChannel(userID,shopId);
        Message messTemp = messageMapper.dtoToModle(message);
        //set channel
        messTemp.setChannel(channel);
        //set account
        Account acc = new Account();
        acc.setId(senderId);
        messTemp.setAccount(acc);
        //save message
        messageService.saveMessage(messTemp);
        //mask all other message to read;

        log.info(String.format("Message like %s",message.toString()));
    }

    @Async
    void sendNotificationPublic (NotificationDto notification) {

    }

    @Async
    void sendNotificationPrivate (NotificationDto notification) {
        Notification noti = notificationMapper.dtoToModel(notification);
        log.info("Here is noti after mapper {}", noti);
        //check send to shop or account
        Account acc = new Account();
        acc.setId(notification.getReceiveId());
        noti.setAccount(acc);
        String destination = null;
        if(notification.getRole().equalsIgnoreCase(NotifiConstant.NOTI_SHOP_ROLE)) {
            //take out the shop id base on account id
            long shopID = accountService.retrieveShopID(notification.getReceiveId());
            destination = String.format("/notification/%d/shop", shopID);
        }else if (notification.getRole().equalsIgnoreCase(NotifiConstant.NOTI_USER_ROLE)) {
            destination = String.format("/notification/%d/user",notification.getReceiveId());
        } else {
            throw new CustomRuntimeException("400","Receive name not correct!");
        }
        messagingTemplate.convertAndSend(destination, noti);

        //save notification
        notificationService.saveNotify(noti);
    }

}
