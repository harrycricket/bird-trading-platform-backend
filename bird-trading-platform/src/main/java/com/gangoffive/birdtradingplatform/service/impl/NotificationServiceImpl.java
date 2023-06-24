package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.NotificationMapper;
import com.gangoffive.birdtradingplatform.repository.NotificationRepository;
import com.gangoffive.birdtradingplatform.service.NotificationService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    @Override
    public boolean saveNotify(Notification notification) {
        try {
            notificationRepository.save(notification);
            return true;
        }catch (Exception e){
            throw new CustomRuntimeException("400", "Cannot save notification");
        }
    }

    @Override
    public ResponseEntity<?> getNotifications(long id, int pageNumber) {
        long currentTime = System.currentTimeMillis();
        long timeAfter7SevenDay = currentTime - NotifiConstant.TIME_BEFOR_NOTI_LOAD;
        PageRequest page = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "notiDate"));
        var listNotifications = notificationRepository.findAllByNotiDateAfterAndAccount_IdAndRoleIs(new Date(timeAfter7SevenDay)
                ,id , UserRole.USER, page);
        if(!listNotifications.isEmpty()) {
            List<NotificationDto> list = listNotifications.get().map(this::notiModelToDto).toList();
            PageNumberWraper result = new PageNumberWraper();
            result.setPageNumber(listNotifications.getTotalPages());
            result.setLists(list);
            return ResponseEntity.ok(result);
        }
//        return new ResponseEntity<>(new ErrorResponse(ResponseCode.NOT_FOUND_NOTIFICATION_ID.getCode()+"",
//                ResponseCode.NOT_FOUND_NOTIFICATION_ID.getMessage()), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new PageNumberWraper<>(new ArrayList<>(), 0), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<?> getUserUnreadNotification(long userid) {
        try{
            //get unread noti
            long unreadNoti  = notificationRepository.countAllBySeenIsFalseAndAccount_IdAndRoleIs(userid, UserRole.USER);
            log.info("here is number un read {}", unreadNoti);
            JsonObject result = new JsonObject();
            result.addProperty("unread", unreadNoti);
            return ResponseEntity.ok(result.toString());
        }catch (Exception e) {
            return new ResponseEntity<>(new ErrorResponse(ResponseCode.NOT_FOUND_UNREAD_NOTIFICATION.getCode()+"",
                    ResponseCode.NOT_FOUND_UNREAD_NOTIFICATION.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private NotificationDto notiModelToDto (Notification notification) {
        NotificationDto result = notificationMapper.modelToDto(notification);
        if(!notification.isSeen()) {
            notificationRepository.updateNotificationsById(notification.getId());
        }
        return result;
    }
}