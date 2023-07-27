package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.LogOrderFilterDto;
import org.springframework.http.ResponseEntity;

public interface LogOrderService {
    ResponseEntity<?> filterAllLogOrder(LogOrderFilterDto logOrderFilter);
}
