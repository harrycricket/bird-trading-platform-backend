package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.mapper.TypeMapper;
import com.gangoffive.birdtradingplatform.repository.TypeBirdRepository;
import com.gangoffive.birdtradingplatform.service.TypeBirdService;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeBirdServiceImpl implements TypeBirdService {
    private final TypeBirdRepository typeBirdRepository;
    private final TypeMapper typeMapper;

    @Override
    public List<TypeBird> getAllTypeBird() {
        return typeBirdRepository.findAll();
    }

    @Override
    public ResponseEntity<?> createNewBirdType(TypeDto typeDto) {
        if(typeDto != null) {
            TypeBird type = typeMapper.dtoToModelBird(typeDto);
            typeBirdRepository.save(type);
            return ResponseEntity.ok("Create successfully!");
        }
        return ResponseUtils.getErrorResponseBadRequest("Some thing went wrong!");
    }
}
