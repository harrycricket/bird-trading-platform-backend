package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import com.gangoffive.birdtradingplatform.mapper.TypeMapper;
import com.gangoffive.birdtradingplatform.repository.TypeFoodRepository;
import com.gangoffive.birdtradingplatform.service.TypeFoodService;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeFoodServiceImpl implements TypeFoodService {
    private final TypeFoodRepository typeFoodRepository;
    private final TypeMapper typeMapper;

    @Override
    public List<TypeFood> getAllTypeFood() {
        return typeFoodRepository.findAll();
    }

    @Override
    public ResponseEntity<?> createNewFoodType(TypeDto typeDto) {
        if(typeDto != null) {
            TypeFood type = typeMapper.dtoToModelFood(typeDto);
            typeFoodRepository.save(type);
            return ResponseEntity.ok("Create successfully!");
        }
        return ResponseUtils.getErrorResponseBadRequest("Some thing went wrong!");
    }
}
