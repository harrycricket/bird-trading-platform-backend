package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.dto.NotificationDto;
import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.entity.Notification;
import com.gangoffive.birdtradingplatform.entity.Promotion;
import com.gangoffive.birdtradingplatform.mapper.PromotionMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.PromotionRepository;
import com.gangoffive.birdtradingplatform.service.NotificationService;
import com.gangoffive.birdtradingplatform.service.PromotionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    @Override
    public ResponseEntity<?> getAllPromotion() {
        List<Promotion> promotions = promotionRepository.findAll();
        List<PromotionDto> promotionDtoList = promotions.stream()
                .filter(promotion -> promotion.getStartDate().compareTo(new Date()) <= 0
                        && promotion.getEndDate().after(new Date()))
                .map(promotion -> {
                    PromotionDto promotionDto = promotionMapper.toDto(promotion);
                    promotionDto.setEndDate(promotion.getEndDate().getTime());
                    return promotionDto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDtoList);
    }

    @Override
    public ResponseEntity<?> createPromotion(PromotionDto createPromotion) {
        if (createPromotion.getEndDate() - createPromotion.getStartDate() > 0) {
            Date startDate =  new Date(createPromotion.getStartDate());
            if (startDate.before(new Date())) {
                Promotion promotion = promotionMapper.modelToDto(createPromotion);
                promotion.setStartDate(new Date(createPromotion.getStartDate()));
                promotion.setEndDate(new Date(createPromotion.getEndDate()));
                if (createPromotion.getType().equals("SHIPPING")){
                    promotion.setDiscount(0);
                }
                promotion.setUsed(0);
                promotionRepository.save(promotion);
                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.CREATED.value()))
                        .successMessage("Create promotion successfully.")
                        .build();
                //send notification for all user
                Optional<List<Long>> listUserId = accountRepository.findAllAccountIdInActive();
                if(listUserId.isPresent() && listUserId.get().size() > 0){
                    NotificationDto noti = new NotificationDto();
                    noti.setName(NotifiConstant.NEW_PROMOTION_NAME);
                    noti.setNotiText(String.format(NotifiConstant.NEW_PROMOTION_CONTENT, createPromotion.getType(),
                            startDate.toString(), new Date(createPromotion.getEndDate()).toString(),createPromotion.getUsageLimit()));
                    noti.setRole(NotifiConstant.NOTI_USER_ROLE);
                    notificationService.pushNotificationForListUserID(listUserId.get(), noti);
                }
                return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.CONFLICT))
                        .errorMessage("Star date no invalid")
                        .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
            }
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.CONFLICT))
                .errorMessage("End date < Start date")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
}
