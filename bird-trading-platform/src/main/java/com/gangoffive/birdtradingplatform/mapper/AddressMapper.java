package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.AddressDto;
import com.gangoffive.birdtradingplatform.dto.AddressInfoDto;
import com.gangoffive.birdtradingplatform.entity.Address;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddressMapper {
    @Mapping(target = "id", source = "id")
    @Mapping(target = "address", source = "address")
    AddressDto toDto(Address address);

    AddressInfoDto toAddressInfoDto(Address address);
}
