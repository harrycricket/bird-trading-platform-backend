package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Notification;
import org.springframework.http.ResponseEntity;

public interface NotificationService {
    public boolean saveNotify(Notification notification);

    ResponseEntity<?> getNotifications(long id, int pageNumber);
}
