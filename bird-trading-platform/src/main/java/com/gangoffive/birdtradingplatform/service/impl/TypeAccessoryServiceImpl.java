package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.repository.TypeAccessoryRepository;
import com.gangoffive.birdtradingplatform.service.TypeAccessoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeAccessoryServiceImpl implements TypeAccessoryService {
    private final TypeAccessoryRepository typeAccessoryRepository;
    @Override
    public List<TypeAccessory> getAllTypeAccessory() {
        return typeAccessoryRepository.findAll();
    }
}
