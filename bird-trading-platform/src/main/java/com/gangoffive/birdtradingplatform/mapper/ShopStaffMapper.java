package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.ShopStaffDto;
import com.gangoffive.birdtradingplatform.entity.ShopStaff;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ShopStaffMapper {

    ShopStaffDto modelToDto(ShopStaff shopStaff);
}
