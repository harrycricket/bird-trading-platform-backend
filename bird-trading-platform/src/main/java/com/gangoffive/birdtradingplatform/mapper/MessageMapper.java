package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.common.MessageConstant;
import com.gangoffive.birdtradingplatform.dto.MessageDto;
import com.gangoffive.birdtradingplatform.entity.Message;
import com.gangoffive.birdtradingplatform.enums.MessageStatus;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageMapper {
    @Mapping(target = "messageText", source = "content")
    @Mapping(target = "status", expression = "java(mapStringToEnum(messageDto.getStatus()))")
    @Mapping(target = "timestamp", source = "date")
    Message dtoToModle (MessageDto messageDto);

    @Mapping(target = "content", source = "messageText")
    @Mapping(target = "date", source = "timestamp")
    @Mapping(target = "userID", source = "account.id")
    @Mapping(target = "status", expression = "java(mapEnumToString(message.getStatus()))" )
    @Mapping(target = "id", source = "id")
    MessageDto modelToDto (Message message);

    default MessageStatus mapStringToEnum(String source) {
        if(source.equalsIgnoreCase(MessageConstant.MESSAGE_STATUS_SENT)){
            return MessageStatus.SENT;
        }
        return MessageStatus.SENT;
    }

    default String mapEnumToString(MessageStatus status) {
        if(status.name().equalsIgnoreCase(MessageStatus.SENT.name())) {
            return "SENT";
        } else if (status.name().equalsIgnoreCase(MessageStatus.SEEN.name())) {
            return "SEEN";
        } else if (status.name().equalsIgnoreCase(MessageStatus.DELIVERED.name())) {
            return "DELIVERED";
        }
        return "MESSAGE";
    }


}
