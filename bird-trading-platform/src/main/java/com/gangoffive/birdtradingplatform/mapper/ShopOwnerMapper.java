package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.ShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopOwnerMapper {
    ShopOwnerDto modelToDto (ShopOwner shopOwner);
}
