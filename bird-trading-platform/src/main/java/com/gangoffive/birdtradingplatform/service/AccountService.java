package com.gangoffive.birdtradingplatform.service;

import org.springframework.stereotype.Service;

import com.gangoffive.birdtradingplatform.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
}
