package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TypeAccessoryService {
    List<TypeAccessory> getAllTypeAccessory();

    ResponseEntity<?> createNewAccessoryType(TypeDto typeDto);
}
