package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.FoodDto;
import com.gangoffive.birdtradingplatform.entity.Food;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface FoodMapper {

    @Mapping(target = "id", source = "food.id")
    @Mapping(source = "food.shopOwner.id", target = "shopId")
    @Mapping(source = "food.typeFood", target = "typeFood")
    FoodDto toDto (Food food);

    @InheritInverseConfiguration
    Food toModel (FoodDto foodDto);
}
