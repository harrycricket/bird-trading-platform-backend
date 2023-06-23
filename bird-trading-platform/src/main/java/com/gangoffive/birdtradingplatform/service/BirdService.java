package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.dto.ProductFilterDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface BirdService {
    List<BirdDto> retrieveAllBird();

    ResponseEntity<?> retrieveBirdsByShopId(Long shopId, int pageNumber);

    ResponseEntity<?> retrieveBirdByPageNumber(int pageNumber);

    List<BirdDto> findBirdByName(String name);

    void updateBird(BirdDto birdDto);

    void deleteBirdById(Long id);

    List<BirdDto> findTopBirdProduct();
}
