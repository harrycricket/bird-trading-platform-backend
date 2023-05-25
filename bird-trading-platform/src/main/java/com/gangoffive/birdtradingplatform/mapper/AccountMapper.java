package com.gangoffive.birdtradingplatform.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.entity.Account;

@Mapper(componentModel = "spring")
public interface AccountMapper {
		
	Account toEntity(AccountDto accountDto);
	
//	@Mapping(source = "account.id",target = "id")
//	@Mapping(source = "account.email",target = "email")
	AccountDto toDto(Account account);
}
