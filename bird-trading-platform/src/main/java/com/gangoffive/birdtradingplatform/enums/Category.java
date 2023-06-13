package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public enum Category {
    BIRD(1, new BirdDto().getClass().getSimpleName()),
    FOOD(2, new FoodDto().getClass().getSimpleName() ),
    ACCESSORY(3, new AccessoryDto().getClass().getSimpleName());

    private final int categoryId;

    private final String categoryName;

    Category(int categoryId, String categoryName) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public static int getCategoryIdByName(String categoryName) {
        List<Category> lists = Arrays.asList(Category.values());
        int result;
        try {
            result = lists.stream()
                    .filter(
                            item -> item.getCategoryName().equals(categoryName)
                    )
                    .map(
                            item -> item.getCategoryId()
                    )
                    .findFirst().get();
        }catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this catagoryid");
        }
        return result;
    }
    public static String getCategoryNameById(int id) {
        List<Category> lists = Arrays.asList(Category.values());
        String result;
        try {
            result = lists.stream().filter(item -> item.getCategoryId()==id)
                    .map(item -> item.getCategoryName()).findFirst().get();
        }catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this catagory");
        }
        return result;
    }
}
