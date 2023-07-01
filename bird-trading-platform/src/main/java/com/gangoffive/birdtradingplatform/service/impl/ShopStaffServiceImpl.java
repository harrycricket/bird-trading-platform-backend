package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.repository.ShopStaffRepository;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.service.ShopStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShopStaffServiceImpl implements ShopStaffService {
    private final ShopStaffRepository shopStaffRepository;
    private final ShopOwnerService ownerService;
    @Override
    public ResponseEntity<?> updateStaus(ChangeStatusListIdDto updateStatusDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        long shopId = ownerService.getShopIdByEmail(email);
        if(updateStatusDto.getStatus() == -2 || updateStatusDto.getStatus() == 1) {
            AccountStatus accountStatus = AccountStatus.getAccountStatus(updateStatusDto.getStatus());
            int result = shopStaffRepository.updateStatusWithShopId(accountStatus,updateStatusDto.getIds(),shopId);
            if(updateStatusDto.getIds().size() == result) {
                ResponseEntity.ok("Update successful");
            }else {
                return new ResponseEntity<>(ErrorResponse.builder().errorCode("400")
                        .errorMessage(String.format("Update fail %d account", result)),
                        HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(ErrorResponse.builder().errorCode("400")
                .errorMessage(String.format("Something went wrong")),
                HttpStatus.BAD_REQUEST);
    }
}
