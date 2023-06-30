package com.gangoffive.birdtradingplatform.service.impl;

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
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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
            PageNumberWraper pageNumberWraper = new PageNumberWraper();
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
            pageNumberWraper.setLists(reversedList);
            pageNumberWraper.setPageNumber(listMessage.getTotalPages());
            return ResponseEntity.ok(pageNumberWraper);
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
    public String getListUserInChannel(int pageNumber) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        var acc = accountRepository.findByEmail(email);
        if(acc.isPresent()) {
            try {
                long shopId = acc.get().getShopOwner().getId();
                PageRequest page = PageRequest.of(pageNumber, MessageConstant.CHANNEL_PAGING_SIZE,
                        Sort.by(Sort.Direction.DESC, "lastedUpdate"));
                Page<Channel> channels = channelRepository.findByShopOwner_Id(shopId, page);
                List<JsonObject> result = channels.getContent().stream()
                        .map(a -> this.createUserListWithUnread(a, acc.get().getShopOwner().getId())).toList();
                return this.createPageNumberWarrpper(result, channels.getTotalPages()).toString();
            }catch (Exception e) {
                throw new CustomRuntimeException("400", "Have no channel");
            }
        }
        return null;
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
