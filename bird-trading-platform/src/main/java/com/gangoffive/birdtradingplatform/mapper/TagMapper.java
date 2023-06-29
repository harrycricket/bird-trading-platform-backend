package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.TagDto;
import com.gangoffive.birdtradingplatform.entity.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TagMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TagDto modelToDto(Tag tag);
}
