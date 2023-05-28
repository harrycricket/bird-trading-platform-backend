package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.mapper.FoodMapper;
import com.gangoffive.birdtradingplatform.repository.FoodRepository;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.FoodService;
import com.gangoffive.birdtradingplatform.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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

    @Override
    public List<FoodDto> retrieveAllFood() {
        List<FoodDto> lists = foodRepository.findAll().stream()
                .map(this::apply).
                collect(Collectors.toList());
        return lists;
    }

    @Override
    public List<FoodDto> retrieveFoodByPagenumber(int pageNumber) {
        if (pageNumber > 0) {
            pageNumber = pageNumber - 1;
            PageRequest page = PageRequest.of(pageNumber, 8);
            List<FoodDto> lists = foodRepository.findAll(page).getContent().stream()
                    .map(this::apply).
                    collect(Collectors.toList());
            return lists;
        } else try {
            throw new Exception("Page number cannot less than 1");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


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

    private FoodDto apply(Food food) {
        var tmp = foodMapper.toDto((Food) food);
        tmp.setStar(productService.CalculationRating(food.getOrderDetails()));
        tmp.setDiscountRate(productService.CalculateSaleOff(food.getPromotionShops(), food.getPrice()));
        return tmp;
    }
}
