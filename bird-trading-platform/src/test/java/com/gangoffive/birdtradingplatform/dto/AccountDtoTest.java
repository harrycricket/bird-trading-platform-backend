package com.gangoffive.birdtradingplatform.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class AccountDtoTest {
	
	@Autowired
	private AccountMapper accountMapper;
	
	@Autowired
	private AccountRepository accountRepository;
	
	@Test
	void TestAccountDtoMapper () {
		Account acc = accountRepository.findById(1L).orElse(null);
		AccountDto accountDto = accountMapper.toDto(acc);
		log.info(accountDto.toString());		
	}
}
