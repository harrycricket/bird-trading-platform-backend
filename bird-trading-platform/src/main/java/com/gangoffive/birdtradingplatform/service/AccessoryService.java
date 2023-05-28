package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface AccessoryService {
    List<AccessoryDto> retrieveAllAccessory();
    ResponseEntity<?> retrieveAccessoryByPageNumber(int pageNumber);
    List<AccessoryDto> findAccessoryByName(String name);
}
