package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.entity.TypeBird;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TypeBirdService {
    List<TypeBird> getAllTypeBird ();

    ResponseEntity<?> createNewBirdType(TypeDto typeDto);

}
