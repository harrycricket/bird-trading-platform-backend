package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;

import java.util.List;

public interface AccessoryService {
    List<AccessoryDto> retrieveAllAccessory();

    List<AccessoryDto> retrieveAllAccessory(int pageNumber);

    List<AccessoryDto> findAccessoryByName(String name);

    void updateAccessory(AccessoryDto accessoryDto);

    void deleteAccessoryById(Long id);
}
