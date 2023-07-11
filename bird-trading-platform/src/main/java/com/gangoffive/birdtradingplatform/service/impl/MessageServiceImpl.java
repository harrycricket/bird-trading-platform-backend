package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.MessageConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.MessageDto;
import com.gangoffive.birdtradingplatform.entity.Channel;
import com.gangoffive.birdtradingplatform.entity.Message;
import com.gangoffive.birdtradingplatform.enums.MessageStatus;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.MessageMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.ChannelRepository;
import com.gangoffive.birdtradingplatform.repository.MessageRepository;
import com.gangoffive.birdtradingplatform.repository.ShopOwnerRepository;
import com.gangoffive.birdtradingplatform.service.ChannelService;
import com.gangoffive.birdtradingplatform.service.MessageService;
import com.google.gson.Gson;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final AccountRepository accountRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final ChannelService channelService;
    private final ChannelRepository channelRepository;
    @Override
    public boolean saveMessage(Message message) {
        try{
            messageRepository.save(message);
        }catch (Exception e){
            return false;
        }
        return true;
    }

    @Override
    public ResponseEntity<?> getListMessageByChannelId(long channelId, int pageNumber, long id, boolean isShop) {
        PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_MESSAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "timestamp"));
        var listMessage = messageRepository.findByChannel_Id(channelId, pageRequest);
        if(listMessage != null) {
            PageNumberWrapper pageNumberWrapper = new PageNumberWrapper();
            List<MessageDto> result = new ArrayList<>();
            if(isShop) {
                var shopOwner = shopOwnerRepository.findById(id);
                if(shopOwner.isPresent()) {
                    long accountId = shopOwner.get().getAccount().getId();
                    result = listMessage.getContent().stream()
                            .map(message -> this.messageToDto(message, accountId)).toList();
                }
            }else{
                result = listMessage.getContent().stream()
                        .map(message -> this.messageToDto(message, id)).toList();
            }

            List<MessageDto> reversedList = new ArrayList<>(result);
            Collections.reverse(reversedList);
            pageNumberWrapper.setLists(reversedList);
            pageNumberWrapper.setPageNumber(listMessage.getTotalPages());
            return ResponseEntity.ok(pageNumberWrapper);
        }
        return null;
    }

    private MessageDto messageToDto (Message message, long id) {
        //only other message change to seen
        if(message.getAccount().getId() != id) {
            message.setStatus(MessageStatus.SEEN);
            //save all to read
            messageRepository.save(message);
        }
        return messageMapper.modelToDto(message);
    }

    @Override
    public boolean maskAllSeen(long senderId, long channelId) {
        log.info(String.format("Here is sender id %d channelid %d", senderId, channelId));
        try{
            messageRepository.updateStatusToSeen(MessageStatus.SEEN.name(),channelId, senderId,MessageStatus.SENT.name());
        }catch (Exception e) {
            throw new CustomRuntimeException("400", "Something went wrong");
        }

        return false;
    }

    @Override
    public String getListUserInChannel(int pageNumber, long shopId) {
        try {
            PageRequest page = PageRequest.of(pageNumber, MessageConstant.CHANNEL_PAGING_SIZE,
                    Sort.by(Sort.Direction.DESC, "lastedUpdate"));
            Page<Channel> channels = channelRepository.findByShopOwner_Id(shopId, page);
            List<JsonObject> result = channels.getContent().stream()
                    .map(a -> this.createUserListWithUnread(a, shopId)).toList();
            return this.createPageNumberWarrpper(result, channels.getTotalPages()).toString();
        }catch (Exception e) {
            throw new CustomRuntimeException("400", "Have no channel");
        }
    }

    @Override
    public ResponseEntity<?> getTotalNumberUnreadMessageUser(long userid) {
        var acc = accountRepository.findById(userid);
        if(acc.isPresent()) {
            List<Channel> channelList = acc.get().getChannels();
            Long totalUnread = messageRepository.countByAccount_IdNotInAndStatusInAndChannelIn(Arrays.asList(userid), MessageConstant.STATUS_UNREAD, channelList);
            JsonObject numberUnread = new JsonObject();
            if(totalUnread != null){
                numberUnread.addProperty("totalUnread", totalUnread);
            }else {
                numberUnread.addProperty("totalUnread", 0);
            }
            return ResponseEntity.ok(numberUnread.toString());
        }else {
            return new ResponseEntity<>(ErrorResponse.builder().errorCode("400").errorMessage("This shop have no shop").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> getTotalNumberUnreadMessageShop(long shopid) {
        var shop  = shopOwnerRepository.findById(shopid);
        if(shop.isPresent()){
            long accountID = shop.get().getAccount().getId();
            Long totalUnread = messageRepository.countByAccount_IdNotInAndStatusInAndChannelIn(Arrays.asList(accountID),
                    MessageConstant.STATUS_UNREAD, shop.get().getChannels());
            JsonObject numberUnread = new JsonObject();
            if(totalUnread != null){
                numberUnread.addProperty("totalUnread", totalUnread);
            }else {
                numberUnread.addProperty("totalUnread", 0);
            }
            return ResponseEntity.ok(numberUnread.toString());
        }else{
            return new ResponseEntity<>(ErrorResponse.builder().errorCode("400").errorMessage("This shop have no shop").build(),
                    HttpStatus.BAD_REQUEST);
        }
    }

    private JsonObject createUserListWithUnread(Channel channel, long shopId) {
        if(channel != null) {
            long userId = channel.getAccount().getId();
            long channelId = channel.getId();
            String userAvatar = channel.getAccount().getImgUrl();
            String name = channel.getAccount().getFullName();
            int unread  = channelService.getMessageUnreadByUserAndShopForShopOwner(userId, shopId);
            //create an json object
            JsonObject result = new JsonObject();
            result.addProperty("userId", userId);
            result.addProperty("channelId", channelId);
            result.addProperty("userName", name);
            result.addProperty("userAvatar", userAvatar);
            result.addProperty("unread", unread);
            return result;
        }
        return null;
    }

    private JsonObject createPageNumberWarrpper (List<JsonObject> jsonObject, int pageNumber) {
        Gson gson = new Gson();
        String listJson = gson.toJson(jsonObject);
        JsonObject warrpperJson = new JsonObject();
        warrpperJson.add("lists", JsonParser.parseString(listJson).getAsJsonArray());
        warrpperJson.addProperty("pageNumber", pageNumber);
        return warrpperJson;
    }
}
