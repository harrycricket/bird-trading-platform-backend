package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PromotionShopMapper {
    PromotionShopDto modelToDto (PromotionShop promotionShop);

    PromotionShop dtoToModel (PromotionShopDto  promotionShopDto);
}
