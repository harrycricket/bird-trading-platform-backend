package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.service.AccountService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import com.gangoffive.birdtradingplatform.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
	private final AccountRepository accountRepository;
	private final AccountMapper accountMapper;
}
