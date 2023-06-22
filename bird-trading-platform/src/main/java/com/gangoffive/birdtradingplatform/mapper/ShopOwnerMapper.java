package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.ShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShopOwnerMapper {
    @Mapping(target = "address.id", source = "address.id")
    @Mapping(target = "address.address", source = "address.address")
    ShopOwnerDto modelToDto (ShopOwner shopOwner);
}
