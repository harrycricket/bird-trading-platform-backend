package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Date;

@Mapper(componentModel = "spring")
public interface PromotionShopMapper {
    @Mapping(target = "startDate", source = "startDate.time")
    @Mapping(target = "endDate",  source = "endDate.time")
    PromotionShopDto modelToDto (PromotionShop promotionShop);

//    PromotionShop dtoToModel (PromotionShopDto  promotionShopDto);
}
