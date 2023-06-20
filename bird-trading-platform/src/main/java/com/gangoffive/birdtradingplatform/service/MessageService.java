package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Message;
import org.springframework.http.ResponseEntity;

public interface MessageService {
    boolean saveMessage (Message message);

    ResponseEntity<?> getListMessageByChannelId (long channelId, int pageNumber, long id);

    boolean maskAllRead(long senderId, long shopID);
}
