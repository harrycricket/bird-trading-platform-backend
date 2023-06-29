package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Channel;

public interface ChannelService {
    public Channel getAndSaveChannel (long userId, long shopId);

    public int getMessageUnreadByUserAndShop (long userId, long shopId);

    int getMessageUnreadByUserAndShopForShopOwner(long userId, long shopId);
}
