package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.service.FoodService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    @GetMapping("foods/by-shop-id")
    public ResponseEntity<?> retrieveAllProduct(@RequestParam int pageNumber, @RequestParam Long shopId) {
        return foodService.retrieveFoodsByShopId(shopId, pageNumber);
    }

    @GetMapping("foods/pages/{pageNumber}")
    public ResponseEntity<? extends Object> retrieveFoodByPageNumber(@PathVariable int pageNumber) {
        return foodService.retrieveFoodByPageNumber(pageNumber);
    }

    @GetMapping("foods/search")
    public List<FoodDto> findFoodByName(@RequestParam String name) {
        return foodService.findFoodByName(name);
    }

    @GetMapping("foods/top-product")
    public List<FoodDto> findTopFood() {
        return foodService.findTopFood();
    }

    @PostMapping("/shopowner/foods/update/{id}")
    @PreAuthorize("hasAnyAuthority('shopowner:update')")
    public void updateFood(@RequestParam FoodDto foodDto) {
        foodService.updateFood(foodDto);
    }

    @DeleteMapping("/shopowner/foods/delete/{id}")
    @RolesAllowed("SHOPOWNER")
    @PreAuthorize("hasAnyAuthority('shopowner:delete')")
    public void deleteFood(@PathVariable("id") Long id) {
        foodService.deleteFoodById(id);
    }
}
