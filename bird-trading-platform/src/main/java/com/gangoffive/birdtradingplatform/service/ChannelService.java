package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Channel;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import org.springframework.http.ResponseEntity;

public interface ChannelService {
    public Channel getAndSaveChannel (long userId, long shopId);

    public int getMessageUnreadByUserAndShop (long userId, long shopId);

    int getMessageUnreadByUserAndShopForShopOwner(long userId, long shopId);

    void setLastedUpdateTime(Long id);

    ResponseEntity<?> getAllShopIdInChanelWithUserId(long userid, int pageNumber);
}
