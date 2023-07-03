package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.ShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Channel;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.MessageStatus;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.ShopOwnerMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.ChannelRepository;
import com.gangoffive.birdtradingplatform.repository.MessageRepository;
import com.gangoffive.birdtradingplatform.repository.ShopOwnerRepository;
import com.gangoffive.birdtradingplatform.service.ChannelService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import com.google.gson.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ChannelServiceImpl implements ChannelService {
    private final ChannelRepository channelRepository;
    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final ShopOwnerMapper shopOwnerMapper;
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

    @Override
    public int getMessageUnreadByUserAndShopForShopOwner(long userId, long shopId) {
        var channel = channelRepository.findChannelByAccount_IdAndShopOwner_Id(userId, shopId);
        if(channel.isPresent()){
            var accountShop = accountRepository.findByShopOwner_Id(shopId);
            if(accountShop.isPresent()) {
                long channelId = channel.get().getId();
                long accountShopID = accountShop.get().getId();
                int unread = messageRepository.countByIdAndListIn(channelId, Arrays.asList(MessageStatus.SENT.name(),
                        MessageStatus.DELIVERED.name()), accountShopID);
                return unread;
            }
        }
        return 0;
    }

    @Override
    public void setLastedUpdateTime(Long id) {
        int result = channelRepository.updateLastedUpdate(new Date(System.currentTimeMillis()), id);
        if (result == 0) {
            throw new CustomRuntimeException("400", "Not found this channel id");
        }
    }

    @Override
    public ResponseEntity<?> getAllShopIdInChanelWithUserId(long userid, int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                Sort.by(Sort.Direction.DESC ,"lastedUpdate"));
        Page<Channel> listChannel = channelRepository.findByAccount_Id(userid, pageRequest);
        if(!listChannel.isEmpty()){
            List<JsonObject> listShopDto = listChannel.stream()
                    .map(channel -> this.shopOwnerToDtoWithUnread(channel.getShopOwner(),userid)).toList();
            JsonObject result = new JsonObject();
            Gson gson = new Gson();
            result.add("lists", JsonParser.parseString(gson.toJson(listShopDto)).getAsJsonArray());
            result.addProperty("pageNumber", listChannel.getTotalPages());
            result.addProperty("totalElement", listChannel.getTotalElements());
            return ResponseEntity.ok(result.toString());
        }
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorMessage("User have no message").errorCode("400").build(), HttpStatus.BAD_REQUEST);
    }

    private List<JsonObject> listShopDto(List<Long> listShopId, long userId) {
        var listShop = shopOwnerRepository.findAllById(listShopId);
        if (listShop != null && !listShop.isEmpty()) {
            List<JsonObject> list = listShop.stream().map(shop -> this.shopOwnerToDtoWithUnread(shop, userId)).toList();
            return list;
        }
        return null;
    }

    private JsonObject shopOwnerToDtoWithUnread(ShopOwner shopOwner, long userId) {
        ShopOwnerDto shopOwnerDto = shopOwnerMapper.modelToDto(shopOwner);
//        String shopDtoJson = JsonUtil.INSTANCE.getJsonString(shopOwner);
        Gson gson = new Gson();
//        String shopDtoJson = gson.toJson(shopOwnerDto, ShopOwnerDto.class);
        String shopDtoJson = gson.toJson(shopOwnerDto);
        JsonObject jsonObject = JsonParser.parseString(shopDtoJson).getAsJsonObject();
        //get out channel id
        int unread = this.getMessageUnreadByUserAndShop(userId, shopOwner.getId());
        jsonObject.addProperty("unread", unread);
        return jsonObject;

    }
}
