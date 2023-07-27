package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.PromotionDto;
import com.gangoffive.birdtradingplatform.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PromotionMapper {
    @Mapping(target = "endDate", source = "endDate.time")
    @Mapping(target = "startDate", source = "startDate.time")
    PromotionDto toDto(Promotion promotion);

    @Mapping(target = "endDate.time", source = "endDate")
    @Mapping(target = "startDate.time", source = "startDate")
    Promotion dtoToModel(PromotionDto promotionDto);
}
