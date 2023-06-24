package com.gangoffive.birdtradingplatform.controller;


import com.gangoffive.birdtradingplatform.common.KafkaConstant;
import com.gangoffive.birdtradingplatform.dto.MessageDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.ChannelService;
import com.gangoffive.birdtradingplatform.service.MessageService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
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
    public String getShop (@PathVariable long userid) {
        List<Long> listShopId = accountService.getAllChanelByUserId(userid);
        List<String> result = shopOwnerService.listShopDto(listShopId, userid);
        if(result != null ){
            return StringEscapeUtils.unescapeJson(result.toString());
        }else {
            return new ArrayList<>().toString();
        }
    }

    @GetMapping("/users/{userid}/messages")
    public ResponseEntity<?> getMessage (@PathVariable long userid, @RequestParam long shopId) {
        long channelID = channelService.getAndSaveChannel(userid, shopId).getId();
        return messageService.getListMessageByChannelId(channelID,1, userid);
    }
}