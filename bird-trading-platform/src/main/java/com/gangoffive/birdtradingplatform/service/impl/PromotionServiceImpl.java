package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.entity.Promotion;
import com.gangoffive.birdtradingplatform.mapper.PromotionMapper;
import com.gangoffive.birdtradingplatform.repository.PromotionRepository;
import com.gangoffive.birdtradingplatform.service.PromotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

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
            if (createPromotion.getStartDate() - new Date().getTime() >0) {
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
