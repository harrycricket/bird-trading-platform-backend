package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.enums.AuthProvider;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.gangoffive.birdtradingplatform.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final AccountMapper accountMapper;

	public String createAccount(AccountDto accountDto) {
		if (accountDto.getMatchingPassword().equals(accountDto.getPassword())){
			Optional<Account> temp = accountRepository.findByEmail(accountDto.getEmail());
			if(!temp.isPresent()) {
				Account acc = accountMapper.toModel(accountDto);
				acc.setRole(UserRole.USER);
				acc.setEnable(false);
				acc.setProvider(AuthProvider.local);
				accountRepository.save(acc);
				return "Register Successfully!";
			}else{
				return "The email has already been used!";
			}
		}
		return "Something went wrong!";
	}
}
