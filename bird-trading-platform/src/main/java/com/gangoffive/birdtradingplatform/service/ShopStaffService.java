package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AuthenticationRequestDto;
import com.gangoffive.birdtradingplatform.dto.AuthenticationShopStaffRequest;
import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import org.springframework.http.ResponseEntity;

public interface ShopStaffService {
    ResponseEntity<?> updateStatus(ChangeStatusListIdDto updateStatusDto);

    ResponseEntity<?> authenticateStaffAccount(AuthenticationShopStaffRequest request);
}
