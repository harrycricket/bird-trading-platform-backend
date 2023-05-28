package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.exception.ErrorResponse;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import com.gangoffive.birdtradingplatform.repository.FoodRepository;
import com.gangoffive.birdtradingplatform.service.FoodService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService {
    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;
    private final ProductService productService;

    @Override
    public List<FoodDto> retrieveAllFood() {
        List<FoodDto> lists = foodRepository.findAll().stream()
                .map(this::apply).
                collect(Collectors.toList());
        return lists;
    }

    @Override
    public ResponseEntity<?> retrieveFoodByPagenumber(int pageNumber) {
        if(pageNumber > 0){
            pageNumber = pageNumber - 1;
            PageRequest page = PageRequest.of(pageNumber, 8);
            Page<Food> pageAble = foodRepository.findAll(page);
            List<FoodDto> lists = pageAble.getContent().stream()
                    .map(this::apply).
                    collect(Collectors.toList());
            PageNumberWraper<FoodDto> result = new PageNumberWraper<>(lists, pageAble.getTotalPages());
            return ResponseEntity.ok(result);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error,HttpStatus.BAD_REQUEST);
    }

    @Override
    public List<FoodDto> findFoodByName(String name) {
        List<FoodDto> lists = foodRepository
                .findByNameLike("%" + name + "%")
                .get()
                .stream()
                .map(this::apply).collect(Collectors.toList());
        return lists;
    }

    private FoodDto apply(Food food) {
        var tmp = foodMapper.toDto((Food) food);
        tmp.setStar(productService.CalculationRating(food.getOrderDetails()));
        tmp.setDiscountRate(productService.CalculateSaleOff(food.getPromotionShops(), food.getPrice()));
        return tmp;
    }
}
