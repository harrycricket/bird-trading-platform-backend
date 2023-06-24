package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.enums.MessageStatus;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    @Mapping(target = "notiText", source = "notiText")
    @Mapping(target = "notiDate", source = "notiDate")
    @Mapping(target = "seen", source = "seen")
    @Mapping(target = "role", expression = "java(mapStringToEnum(notificationDto.getRole()))")
    Notification dtoToModel (NotificationDto notificationDto);

    default UserRole mapStringToEnum(String source) {
        if(source.equalsIgnoreCase(NotifiConstant.NOTI_SHOP_ROLE)){
            return UserRole.SHOPOWNER;
        }
        return UserRole.USER;
    }
}
