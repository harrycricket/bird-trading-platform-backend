package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    public boolean saveNotify(Notification notification);

    ResponseEntity<?> getNotifications(long id, int pageNumber, UserRole user);

    ResponseEntity<?> getUserUnreadNotification(long userid, UserRole user);
}
