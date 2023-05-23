package com.gangoffive.birdtradingplatform.controller;

import org.springframework.web.bind.annotation.RestController;

import com.gangoffive.birdtradingplatform.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {
	public final AccountService accountService;
}
