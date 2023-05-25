package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FoodController {
    private final FoodService foodService;

    @GetMapping("v1/foods")
    public List<FoodDto> retriceveAllFood() {
        return foodService.retriceveAllFood();
    }


    @GetMapping("v1/foods/pages/{pageNumber}")
    public List<FoodDto> retrieveFoodByPagenumber(@PathVariable int pageNumber){
        return foodService.retrieveFoodByPagenumber(pageNumber);
    }

    @GetMapping("v1/foods/search")
    public List<FoodDto> findFoodByName(@RequestParam String name){
        return foodService.findFoodByName("%" + name + "%");
    }





}
