package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.BirdDto;

import java.util.List;

public interface BirdService {
    List<BirdDto> retrieveAllBird();

    List<BirdDto> retrieveBirdByPageNumber(int pageNumber);

    List<BirdDto> findBirdByName(String name);

    void updateBird(BirdDto birdDto);

    void deleteBirdById(Long id);
}
