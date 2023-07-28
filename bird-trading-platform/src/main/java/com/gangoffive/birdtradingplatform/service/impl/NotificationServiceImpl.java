package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.KafkaConstant;
import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.mapper.NotificationMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.NotificationRepository;
import com.gangoffive.birdtradingplatform.repository.ShopOwnerRepository;
import com.gangoffive.birdtradingplatform.service.NotificationService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ShopOwnerRepository shopOwnerRepository;
    private final KafkaTemplate kafkaTemplate;
    private final AccountRepository accountRepository;
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
    public ResponseEntity<?> getNotifications(long id, int pageNumber, UserRole role) {
        long currentTime = System.currentTimeMillis();
        long timeAfter7SevenDay = currentTime - NotifiConstant.TIME_BEFOR_NOTI_LOAD;
        PageRequest page = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "notiDate"));
        Page<Notification> listNotifications;
        if(role.equals(UserRole.USER)) {
            listNotifications = notificationRepository.findAllByNotiDateAfterAndAccount_IdAndRoleIs(new Date(timeAfter7SevenDay)
                    ,id , role, page);
        }else {
            long accountIdOfShop = shopOwnerRepository.findById(id).get().getAccount().getId();
            listNotifications = notificationRepository.findAllByNotiDateAfterAndAccount_IdAndRoleIs(new Date(timeAfter7SevenDay)
                    ,accountIdOfShop , role, page);
        }
        if(!listNotifications.isEmpty()) {
            List<NotificationDto> list = listNotifications.get().map(this::notiModelToDto).toList();
            PageNumberWrapper result = new PageNumberWrapper();
            result.setPageNumber(listNotifications.getTotalPages());
            result.setLists(list);
            return ResponseEntity.ok(result);
        }
//        return new ResponseEntity<>(new ErrorResponse(ResponseCode.NOT_FOUND_NOTIFICATION_ID.getCode()+"",
//                ResponseCode.NOT_FOUND_NOTIFICATION_ID.getMessage()), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(new PageNumberWrapper<>(new ArrayList<>(), 0), HttpStatus.ACCEPTED);
    }

    @Override
    public ResponseEntity<?> getUserUnreadNotification(long id, UserRole role) {
        try{
            long unreadNoti = 0;
            if(role.equals(UserRole.USER)) {
                //get unread noto
                unreadNoti  = notificationRepository.countAllBySeenIsFalseAndAccount_IdAndRoleIs(id, role);
            }else {
                long accountOfShop = shopOwnerRepository.findById(id).get().getAccount().getId();
                unreadNoti  = notificationRepository.countAllBySeenIsFalseAndAccount_IdAndRoleIs(accountOfShop, role);
            }

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

    @Override
    @Async
    public boolean pushNotificationForListUserID(List<Long> userIdList, NotificationDto notificationDto){
        boolean result = true;
        for(long id : userIdList) {
            notificationDto.setReceiveId(id);
            notificationDto.setId(System.currentTimeMillis());
            notificationDto.setSeen(false);
            notificationDto.setNotiDate(new Date());
            this.handleSendNotification(notificationDto);
        }
        return result;
    }

    @Override
    public boolean pushNotificationForAUserID(Long userId, NotificationDto notificationDto) {
        if(notificationDto.getRole().equals(NotifiConstant.NOTI_USER_ROLE)
                || notificationDto.getRole().equals(NotifiConstant.NOTI_SHOP_ROLE)){
            notificationDto.setReceiveId(userId);
            notificationDto.setId(System.currentTimeMillis());
            notificationDto.setSeen(false);
            notificationDto.setNotiDate(new Date());
            this.handleSendNotification(notificationDto);
        }else {
            return false;
        }
        return true;
    }

    @Override
    public ResponseEntity<?> handleSendNotification(NotificationDto notification) {
        Notification noti = notificationMapper.dtoToModel(notification);
        notification.setId(System.currentTimeMillis());
        //check send to shop or account
        Account acc = new Account();
        acc.setId(notification.getReceiveId());
        noti.setAccount(acc);
        if(notification.getRole().equalsIgnoreCase(NotifiConstant.NOTI_SHOP_ROLE)) {
            //take out the shop id base on account id
            long shopID = accountRepository.findById(notification.getReceiveId()).get().getShopOwner().getId();
            notification.setReceiveId(shopID);
            this.sendNotification(notification);
        } else if(notification.getRole().equalsIgnoreCase(NotifiConstant.NOTI_USER_ROLE)){
            this.sendNotification(notification);
        }else {
            return ResponseUtils.getErrorResponseBadRequest("Receive name not correct!");
        }
        //save notification
        saveNotify(noti);
        return ResponseEntity.ok("Oke");
    }
    void sendNotification(NotificationDto notificationDto) {
        String notification = JsonUtil.INSTANCE.getJsonString(notificationDto);
        CompletableFuture<SendResult<String, String>> future =
                kafkaTemplate.send(KafkaConstant.KAFKA_PRIVATE_NOTIFICATION, notification);
        try  {
            SendResult<String, String> response = future.get();
            log.info("Record metadata: {}", response.getRecordMetadata());
        }catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private boolean saveNotification(NotificationDto notificationDto) {
        return false;
    }
}
