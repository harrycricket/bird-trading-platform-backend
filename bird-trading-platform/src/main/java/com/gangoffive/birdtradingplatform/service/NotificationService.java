package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface NotificationService {
    public boolean saveNotify(Notification notification);

    ResponseEntity<?> getNotifications(long id, int pageNumber, UserRole user);

    ResponseEntity<?> getUserUnreadNotification(long userid, UserRole user);

    boolean pushNotificationForListUserID(List<Long> userIdList, NotificationDto notificationDto);

    boolean pushNotificationForAUserID(Long userId, NotificationDto notificationDto);
}
