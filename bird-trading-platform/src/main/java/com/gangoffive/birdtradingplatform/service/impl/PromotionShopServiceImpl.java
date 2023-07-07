package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import com.gangoffive.birdtradingplatform.enums.ResponseCode;
import com.gangoffive.birdtradingplatform.mapper.PromotionShopMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.PromotionShopRepository;
import com.gangoffive.birdtradingplatform.service.PromotionShopService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PromotionShopServiceImpl implements PromotionShopService {
    private final PromotionShopRepository promotionShopRepository;
    private final PromotionShopMapper promotionShopMapper;
    private final AccountRepository accountRepository;

    @Override
    public ResponseEntity<?> retrieveAllPromotionShop() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Account> account = accountRepository.findByEmail(username);
        var promotionShopList = promotionShopRepository.findAllByShopOwner_Id(account.get().getShopOwner().getId());
        if (promotionShopList.isPresent()) {
            List<PromotionShopDto> result = promotionShopList.get().stream()
                    .map(a -> promotionShopMapper.modelToDto(a))
                    .toList();
            return ResponseEntity.ok(result);
        }
        return new ResponseEntity<>(ErrorResponse.builder().
                errorMessage(ResponseCode.NOT_FOUND_THIS_SHOP_ID.toString()).
                errorCode(HttpStatus.NOT_FOUND.name()).build(), HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> createNewPromotionShop(PromotionShopDto promotionShop) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        if (
                promotionShop != null
                        && !promotionShop.getName().isEmpty()
                        && !promotionShop.getDescription().isEmpty()
                        && promotionShop.getDiscountRate() > 0
                        && promotionShop.getStartDate() > 0
                        && promotionShop.getEndDate() > 0
        ) {
            PromotionShop promotion = new PromotionShop();
            promotion.setName(promotionShop.getName());
            promotion.setDescription(promotionShop.getDescription());
            promotion.setDiscountRate(promotionShop.getDiscountRate());
            promotion.setStartDate(DateUtils.timeInMillisecondToDate(promotionShop.getStartDate()));
            promotion.setEndDate(DateUtils.timeInMillisecondToDate(promotionShop.getEndDate()));
            promotion.setShopOwner(account.get().getShopOwner());
            promotionShopRepository.save(promotion);
            SuccessResponse successResponse = SuccessResponse.builder()
                    .successCode(String.valueOf(HttpStatus.CREATED.value()))
                    .successMessage("Create promotion shop successfully.")
                    .build();
            return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                    .errorMessage("Promotion shop is null.")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }
}
