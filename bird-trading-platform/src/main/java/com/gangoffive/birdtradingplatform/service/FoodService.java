package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FoodService {
    List<FoodDto> retrieveAllFood();

    ResponseEntity<?> retrieveFoodsByShopId(Long shopId, int pageNumber);

    ResponseEntity<?> retrieveFoodByPageNumber(int pageNumber);

    List<FoodDto> findFoodByName(String name);

    //    Food addNewFood(FoodDto id);
    void updateFood(FoodDto foodDto);

    void deleteFoodById(Long id);

    List<FoodDto> findTopFood();
}
