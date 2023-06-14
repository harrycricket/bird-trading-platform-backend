package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.repository.TypeBirdRepository;
import com.gangoffive.birdtradingplatform.service.TypeBirdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeBirdServiceImpl implements TypeBirdService {
    private final TypeBirdRepository typeBirdRepository;

    @Override
    public List<TypeBird> getAllTypeBird() {
        return typeBirdRepository.findAll();
    }
}
