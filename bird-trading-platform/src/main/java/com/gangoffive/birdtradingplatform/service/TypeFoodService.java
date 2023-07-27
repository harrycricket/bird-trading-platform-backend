package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TypeFoodService {
    List<TypeFood> getAllTypeFood();

    ResponseEntity<?> createNewFoodType(TypeDto typeDto);
}
