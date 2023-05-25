package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.mapper.AccessoryMapper;
import com.gangoffive.birdtradingplatform.repository.AccessoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccessoryService {
    private final AccessoryRepository accessoryRepository;
    private final AccessoryMapper accessoryMapper;
    public List<AccessoryDto> retrieveAllAccessory() {
        List<AccessoryDto> accessories = accessoryRepository
                .findAll()
                .stream()
                .map(
                        product -> {
                            if (product instanceof Accessory)
                                return accessoryMapper.toDto((Accessory) product);
                            return null;
                        }
                )
                .collect(Collectors.toList());
        return accessories;
    }

    public List<AccessoryDto> retrieveAllAccessory(int pageNumber) {
        PageRequest pageRequest = PageRequest.of(pageNumber, 8);
        List<AccessoryDto> accessories = accessoryRepository
                .findAll(pageRequest)
                .stream()
                .map(
                        product -> {
                            if (product instanceof Accessory)
                                return accessoryMapper.toDto((Accessory) product);
                            return null;
                        }
                )
                .collect(Collectors.toList());
        return accessories;
    }

    public List<AccessoryDto> findAccessoryByName(String name) {
        List<AccessoryDto> accessories = accessoryRepository
                .findByNameLike(name)
                .get()
                .stream()
                .map(
                        accessory -> accessoryMapper.toDto(accessory)
                )
                .collect(Collectors.toList());
        return accessories;
    }

}
