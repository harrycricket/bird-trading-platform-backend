package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.MessageConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.MessageDto;
import com.gangoffive.birdtradingplatform.entity.Message;
import com.gangoffive.birdtradingplatform.enums.MessageStatus;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.MessageMapper;
import com.gangoffive.birdtradingplatform.repository.MessageRepository;
import com.gangoffive.birdtradingplatform.service.MessageService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
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
    public ResponseEntity<?> getListMessageByChannelId(long channelId, int pageNumber, long id) {
        if(pageNumber > 0){
            --pageNumber;
        }
        PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_MESSAGE_SIZE,
                Sort.by(Sort.Direction.ASC, "timestamp"));
        var listMessage = messageRepository.findByChannel_Id(channelId, pageRequest);
        if(listMessage != null) {
            PageNumberWraper pageNumberWraper = new PageNumberWraper();
            pageNumberWraper.setLists(listMessage.getContent().stream()
                    .map(message -> this.messageToDto(message, id)).toList());
            pageNumberWraper.setPageNumber(listMessage.getTotalPages());
            return ResponseEntity.ok(pageNumberWraper);
        }
        return null;
    }

    @Override
    public boolean maskAllSeen(long senderId, long channelId) {
        log.info(String.format("Here is sender id %d channelid %d", senderId, channelId));
        try{
            messageRepository.updateStatusToSeen(MessageStatus.SEEN.name(),channelId, senderId,MessageStatus.SENT.name());
        }catch (Exception e) {
//            throw new CustomRuntimeException("400", "Something went wrong");
            e.printStackTrace();
        }

        return false;
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

}
