package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.ChangeStatusListIdDto;
import org.springframework.http.ResponseEntity;

public interface ShopStaffService {
    ResponseEntity<?> updateStaus(ChangeStatusListIdDto updateStatusDto);
}
