package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import com.gangoffive.birdtradingplatform.repository.FoodRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodService {
    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;

    public List<FoodDto> retrieveAllFood() {
        List<FoodDto> lists = foodRepository.findAll().stream()
                .map(food -> {
                    if (food instanceof Food) {
                        return foodMapper.toDto((Food) food);
                    } else {
                        return null;
                    }
                }).
                collect(Collectors.toList());
        return lists;
    }

    public List<FoodDto> retrieveFoodByPagenumber(int pageNumber) {
        PageRequest page = PageRequest.of(pageNumber, 8);
        List<FoodDto> lists = foodRepository.findAll(page).getContent().stream()
                .map(food -> {
                    if (food instanceof Food) {
                        return foodMapper.toDto((Food) food);
                    } else {
                        return null;
                    }
                }).
                collect(Collectors.toList());
        return lists;

    }

    public List<FoodDto> findFoodByName(String name) {
        List<FoodDto> lists = foodRepository
                .findByNameLike(name)
                .get()
                .stream()
                .map(
                        food -> foodMapper.toDto(food)
                ).collect(Collectors.toList());
        return lists;
    }

}
