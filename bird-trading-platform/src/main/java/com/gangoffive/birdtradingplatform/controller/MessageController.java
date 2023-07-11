package com.gangoffive.birdtradingplatform.controller;


import com.gangoffive.birdtradingplatform.common.KafkaConstant;
import com.gangoffive.birdtradingplatform.dto.MessageDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.ChannelService;
import com.gangoffive.birdtradingplatform.service.MessageService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Slf4j
public class MessageController {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ChannelService channelService;
    private final ShopOwnerService shopOwnerService;
    private final AccountService accountService;
    private final MessageService messageService;

    @PostMapping("/users/message/send")
    public ResponseEntity<?> sendMessage (@RequestBody MessageDto messageDto) {
        String message = JsonUtil.INSTANCE.getJsonString(messageDto);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(KafkaConstant.KAFKA_PRIVATE_CHAT, message);
        try  {
            SendResult<String, String> response = future.get();
            log.info("Record metadata: {}", response.getRecordMetadata());
            return new ResponseEntity<>(HttpStatus.OK);
        }catch ( InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/users/{userid}/channels")
    public ResponseEntity<?> getShopInChannel (@PathVariable long userid, @RequestParam("pagenumber") int pageNumber) {
        ResponseEntity<?> shopTemp = channelService.getAllShopIdInChanelWithUserId(userid, pageNumber);
        return shopTemp;
    }

    @GetMapping("/users/{userid}/messages")
    public ResponseEntity<?> getMessageUser (@PathVariable long userid, @RequestParam("shopid") long shopId,
                                         @RequestParam("pagenumber") int pageNumber) {
        long channelID = channelService.getAndSaveChannel(userid, shopId).getId();
        return messageService.getListMessageByChannelId(channelID,pageNumber, userid, false);
    }

    @GetMapping("/users/{userid}/messages/unread")
    public ResponseEntity<?> getNumberUnreadMessageUser (@PathVariable long userid) {
        return messageService.getTotalNumberUnreadMessageUser(userid);
    }

    @GetMapping("/shop-owner/{shopId}/channels")
    public String getUserInChannel(@PathVariable long shopId, @RequestParam("pagenumber") int pageNumber) {
        return messageService.getListUserInChannel(pageNumber, shopId);
    }

    @GetMapping("/shop-owner/{shopId}/messages")
    public ResponseEntity<?> getMessage (@PathVariable long shopId, @RequestParam("userid") long userId,
                                         @RequestParam("pagenumber") int pageNumber) {
        long channelID = channelService.getAndSaveChannel(userId, shopId).getId();
        return messageService.getListMessageByChannelId(channelID ,pageNumber , shopId, true);
    }

    @GetMapping("/shop-owner/{shopid}/messages/unread")
    public ResponseEntity<?> getNumberUnreadMessageShop (@PathVariable long shopid) {
        return messageService.getTotalNumberUnreadMessageShop(shopid);
    }
}
