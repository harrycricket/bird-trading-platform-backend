package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.mapper.TypeMapper;
import com.gangoffive.birdtradingplatform.repository.TypeAccessoryRepository;
import com.gangoffive.birdtradingplatform.service.TypeAccessoryService;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import com.sun.mail.iap.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TypeAccessoryServiceImpl implements TypeAccessoryService {
    private final TypeAccessoryRepository typeAccessoryRepository;
    private final TypeMapper typeMapper;

    @Override
    public List<TypeAccessory> getAllTypeAccessory() {
        return typeAccessoryRepository.findAll();
    }

    @Override
    public ResponseEntity<?> createNewAccessoryType(TypeDto typeDto) {
        if(typeDto != null) {
            TypeAccessory type = typeMapper.dtoToModelAccessory(typeDto);
            typeAccessoryRepository.save(type);
            return ResponseEntity.ok("Create successfully!");
        }
        return ResponseUtils.getErrorResponseBadRequest("Some thing went wrong!");
    }
}
