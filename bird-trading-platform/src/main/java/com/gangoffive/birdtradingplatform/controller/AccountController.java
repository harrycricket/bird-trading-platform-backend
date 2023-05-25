package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.gangoffive.birdtradingplatform.service.AccountService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AccountController {
	public final AccountService accountService;

	public ResponseEntity<String> registerAccount(@RequestBody AccountDto accountDto) {
		String message = accountService.createAccount(accountDto);
		if(message.equalsIgnoreCase("Something went wrong!"))
			return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
		return ResponseEntity.ok(message);

	}
}
