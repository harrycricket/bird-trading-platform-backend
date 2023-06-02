package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.entity.Bird;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BirdMapper{

    @Mapping(target = "id", source = "bird.id")
    @Mapping(source = "bird.typeBird", target = "type")
    @Mapping(source = "bird.shopOwner.id", target = "shopOwner.id")
    @Mapping(source = "bird.shopOwner.shopName", target = "shopOwner.shopName")
    @Mapping(source = "bird.shopOwner.imgUrl", target = "shopOwner.imgUrl", defaultValue = "https://th.bing.com/th/id/R.aeaa38b7aa3046ce9086cc361c820b4c?rik=uVb%2bxcU7Xy6ZzA&pid=ImgRaw&r=0")
    BirdDto toDto(Bird bird);

    @InheritInverseConfiguration
    Bird toModel (BirdDto birdDto);
}
