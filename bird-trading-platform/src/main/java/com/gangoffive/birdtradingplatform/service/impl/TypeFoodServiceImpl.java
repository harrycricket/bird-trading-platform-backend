package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.entity.TypeFood;
import com.gangoffive.birdtradingplatform.repository.TypeFoodRepository;
import com.gangoffive.birdtradingplatform.service.TypeFoodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeFoodServiceImpl implements TypeFoodService {
    private final TypeFoodRepository typeFoodRepository;

    @Override
    public List<TypeFood> getAllTypeFood() {
        return typeFoodRepository.findAll();
    }
}
