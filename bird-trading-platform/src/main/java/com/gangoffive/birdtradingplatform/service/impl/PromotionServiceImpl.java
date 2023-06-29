package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import com.gangoffive.birdtradingplatform.entity.Promotion;
import com.gangoffive.birdtradingplatform.mapper.PromotionMapper;
import com.gangoffive.birdtradingplatform.repository.PromotionRepository;
import com.gangoffive.birdtradingplatform.service.PromotionService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
                .filter(promotion -> promotion.getEndDate().after(new Date()))
                .map(promotion -> {
                    PromotionDto promotionDto = promotionMapper.toDto(promotion);
                    promotionDto.setEndDate(promotion.getEndDate().getTime());
                    return promotionDto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(promotionDtoList);
    }
}
