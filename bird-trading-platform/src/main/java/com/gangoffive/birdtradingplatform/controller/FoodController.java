package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.exception.ErrorResponse;
import com.gangoffive.birdtradingplatform.service.FoodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/foods")
public class FoodController {
    private final FoodService foodService;

    @GetMapping
    public List<FoodDto> retrieveAllFood() {
        return foodService.retrieveAllFood();
    }

    @GetMapping("/pages/{pageNumber}")
    public ResponseEntity<? extends Object> retrieveFoodByPagenumber(@PathVariable int pageNumber){
        return foodService.retrieveFoodByPagenumber(pageNumber);
    }

    @GetMapping("/search")
    public List<FoodDto> findFoodByName(@RequestParam String name){
        return foodService.findFoodByName(name);
    }
}
