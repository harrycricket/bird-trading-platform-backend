package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.ShopStaffDto;
import com.gangoffive.birdtradingplatform.entity.ShopStaff;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShopStaffMapper {

    @Mapping(target = "shopId", source = "shopOwner.id")
    ShopStaffDto modelToDto(ShopStaff shopStaff);
}
