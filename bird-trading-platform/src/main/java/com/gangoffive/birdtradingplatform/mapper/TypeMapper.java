package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.TypeDto;
import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TypeMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @InheritInverseConfiguration
    TypeDto modelToDto(TypeBird type);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @InheritInverseConfiguration
    TypeDto modelToDto(TypeFood type);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @InheritInverseConfiguration
    TypeDto modelToDto(TypeAccessory type);

    TypeAccessory dtoToModelAccessory(TypeDto typeDto);
    TypeFood dtoToModelFood(TypeDto typeDto);
    TypeBird dtoToModelBird(TypeDto typeDto);
}
