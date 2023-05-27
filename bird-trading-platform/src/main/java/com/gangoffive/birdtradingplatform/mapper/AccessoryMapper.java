package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessoryMapper {
    @Mapping(target = "id", source = "accessory.id")
    @Mapping(source = "accessory.typeAccessory", target = "typeAccessory")
    @Mapping(source = "accessory.shopOwner.id", target = "shopOwner.id")
    @Mapping(source = "accessory.shopOwner.shopName", target = "shopOwner.shopName")
    @Mapping(source = "accessory.shopOwner.imgUrl", target = "shopOwner.imgUrl",defaultValue = "https://th.bing.com/th/id/R.aeaa38b7aa3046ce9086cc361c820b4c?rik=uVb%2bxcU7Xy6ZzA&pid=ImgRaw&r=0")
    AccessoryDto toDto (Accessory accessory);

    @InheritInverseConfiguration
    Accessory toModel(AccessoryDto accessoryDto);
}
