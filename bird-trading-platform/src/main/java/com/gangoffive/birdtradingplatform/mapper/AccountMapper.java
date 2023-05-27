package com.gangoffive.birdtradingplatform.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
	@InheritInverseConfiguration
	Account toModel(AccountDto accountDto);

	AccountDto toDto(Account account);
}
