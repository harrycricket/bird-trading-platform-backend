package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.service.FoodService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
//@PreAuthorize("hasRole('SHOPOWNER')")
public class FoodController {
    private final FoodService foodService;

    @GetMapping("foods")
    public List<FoodDto> retrieveAllFood() {
        return foodService.retrieveAllFood();
    }

    @GetMapping("foods/pages/{pageNumber}")
    public List<FoodDto> retrieveFoodByPagenumber(@PathVariable int pageNumber){
        return foodService.retrieveFoodByPagenumber(pageNumber);
    }

    @GetMapping("foods/search")
    public List<FoodDto> findFoodByName(@RequestParam String name){
        return foodService.findFoodByName(name);
    }

    @PostMapping("/shopowner/foods/update/{id}")
    @PreAuthorize("hasAnyAuthority('shopowner:update')")
    public void updateFood(@RequestParam FoodDto foodDto) {
        foodService.updateFood(foodDto);
    }

    @DeleteMapping("/shopowner/foods/delete/{id}")
    @RolesAllowed("SHOPOWNER")
    @PreAuthorize("hasAnyAuthority('shopowner:update')")
    public void deleteFood(@PathVariable("id") Long id) {
        foodService.deleteFoodById(id);
    }
}
