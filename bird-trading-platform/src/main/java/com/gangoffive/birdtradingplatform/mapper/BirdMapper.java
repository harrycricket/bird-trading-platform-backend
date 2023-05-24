package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.entity.Bird;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BirdMapper{

    @Mapping(source = "bird.typeBird.name", target = "typeName")
    @Mapping(source = "bird.shopOwner.id", target = "shopId")
    BirdDto toDto(Bird bird);

    @InheritInverseConfiguration
    Bird toModel (BirdDto birdDto);
}
