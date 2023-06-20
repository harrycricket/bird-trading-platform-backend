package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.repository.NotificationRepository;
import com.gangoffive.birdtradingplatform.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;

    @Override
    public boolean saveNotify(Notification notification) {
        try {
            notificationRepository.save(notification);
            return true;
        }catch (Exception e){
            throw new CustomRuntimeException("400", "Cannot save notification");
        }
    }
}
