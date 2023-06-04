package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import com.gangoffive.birdtradingplatform.repository.FoodRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.FoodService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
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
    private final TagRepository tagRepository;
    private final FoodMapper foodMapper;
    private final ProductService productService;
    private final ProductSummaryService productSummaryService;

    @Override
    public List<FoodDto> retrieveAllFood() {
        List<FoodDto> lists = foodRepository.findAll().stream()
                .map(this::apply).
                collect(Collectors.toList());
        return lists;
    }

    @Override
    public ResponseEntity<?> retrieveFoodByPagenumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest page = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE);
            Page<Food> pageAble = foodRepository.findAll(page);
            List<FoodDto> lists = pageAble.getContent().stream()
                    .map(this::apply).
                    collect(Collectors.toList());
            PageNumberWraper<FoodDto> result = new PageNumberWraper<>(lists, pageAble.getTotalPages());
            return ResponseEntity.ok(result);
        }
        ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                "Page number cannot less than 1");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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

    @Override
    public void updateFood(FoodDto foodDto) {
        foodRepository.save(foodMapper.toModel(foodDto));
    }

    @Override
    public void deleteFoodById(Long id) {
        foodRepository.deleteById(id);
    }

    @Override
    public List<FoodDto> findTopFood() {
        List<Food> lists = foodRepository.findAllById(productSummaryService.getIdTopFood());
        if(lists != null) {
            List<FoodDto> listDto = lists.stream().map(this::apply).toList();
            return listDto;
        }
        return null;
    }

    private FoodDto apply(Food food) {
        var tmp = foodMapper.toDto((Food) food);
        tmp.setStar(productService.CalculationRating(food.getOrderDetails()));
        tmp.setDiscountRate(productService.CalculateSaleOff(food.getPromotionShops(), food.getPrice()));
        return tmp;
    }
}
