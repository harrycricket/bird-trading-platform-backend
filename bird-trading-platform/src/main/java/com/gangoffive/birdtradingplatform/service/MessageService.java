package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.MessageDto;
import com.gangoffive.birdtradingplatform.entity.Message;
import org.springframework.http.ResponseEntity;

public interface MessageService {
    boolean saveMessage (Message message);

    ResponseEntity<?> getListMessageByChannelId (long channelId, int pageNumber, long id, boolean isShop);

    boolean maskAllSeen(long senderId, long channelId);

    String getListUserInChannel(int pageNumber, long shopId);

    ResponseEntity<?> getTotalNumberUnreadMessageUser(long userid);

    ResponseEntity<?> getTotalNumberUnreadMessageShop(long shopid);

    ResponseEntity<?> handleSendMessage(MessageDto messageDto);
}
