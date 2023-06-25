package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.ShopInfoDto;
import com.gangoffive.birdtradingplatform.dto.ShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ShopOwnerMapper {
    @Mapping(target = "address.id", source = "address.id")
    @Mapping(target = "address.address", source = "address.address")
    @Mapping(target = "imgUrl", source = "avatarImgUrl")
    ShopOwnerDto modelToDto (ShopOwner shopOwner);


    @Mapping(target = "address.id", source = "address.id")
    @Mapping(target = "address.address", source = "address.address")
    @Mapping(target = "createdDate", source = "createdDate.time")
    @Mapping(target = "role", expression = "java(mapEnumToInt(shopOwner.getAccount().getRole()))")
    ShopInfoDto modelToShopInfoDto (ShopOwner shopOwner);

    default int mapEnumToInt(UserRole role) {
       return role.ordinal() + 1;
    }
}
