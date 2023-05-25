package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.AccessoryDto;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccessoryMapper {
    @Mapping(target = "id", source = "accessory.id")
    @Mapping(target = "shopId", source = "accessory.shopOwner.id")
    @Mapping(target = "typeAccessory", source = "accessory.typeAccessory")
    AccessoryDto toDto (Accessory accessory);

    @InheritInverseConfiguration
    Accessory toModel(AccessoryDto accessoryDto);
}
