package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import org.springframework.http.ResponseEntity;

import java.util.List;


public interface BirdService {
    List<BirdDto> retrieveAllBird();
    ResponseEntity<?> retrieveBirdByPageNumber(int pageNumber);
    List<BirdDto> findBirdByName(String name);
}
