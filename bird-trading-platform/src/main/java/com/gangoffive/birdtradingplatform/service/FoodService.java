package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.FoodDto;

import java.util.List;

public interface FoodService {
    List<FoodDto> retrieveAllFood();

    List<FoodDto> retrieveFoodByPagenumber(int pageNumber);

    List<FoodDto> findFoodByName(String name);

    //    Food addNewFood(FoodDto id);
    void updateFood(FoodDto foodDto);

    void deleteFoodById(Long id);
}
