package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Channel;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.MessageStatus;
import com.gangoffive.birdtradingplatform.repository.ChannelRepository;
import com.gangoffive.birdtradingplatform.repository.MessageRepository;
import com.gangoffive.birdtradingplatform.service.ChannelService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    @Override
    public Channel getAndSaveChannel(long userId, long shopId) {
        var channel = channelRepository.findChannelByAccount_IdAndShopOwner_Id(userId, shopId);
        if(channel.isPresent()){
            return channel.get();
        }else {
            Channel temp = new Channel();
            Account account = new Account();
            account.setId(userId);
            //create shop
            ShopOwner shopOwner = new ShopOwner();
            shopOwner.setId(shopId);
            temp.setAccount(account);
            temp.setShopOwner(shopOwner);
            channelRepository.save(temp);
            temp = channelRepository.findChannelByAccount_IdAndShopOwner_Id(userId, shopId).get();
            return temp;
        }
    }

    @Override
    public int getMessageUnreadByUserAndShop(long userId, long shopId) {
        var channel = channelRepository.findChannelByAccount_IdAndShopOwner_Id(userId, shopId);
        if(channel.isPresent()){
            long channelId = channel.get().getId();
            int unread = messageRepository.countByIdAndListIn(channelId, Arrays.asList(MessageStatus.SENT.name(),
                    MessageStatus.DELIVERED.name()), userId);
            return unread;
        }
        return 0;
    }
}
