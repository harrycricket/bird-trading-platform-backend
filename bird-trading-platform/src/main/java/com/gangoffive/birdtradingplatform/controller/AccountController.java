package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.impl.AccountServiceImpl;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountServiceImpl accountService;

    @PutMapping("/user/updateprofile")
    @PreAuthorize("hasAnyAuthority('shopowner:put') OR hasAnyAuthority('shopstaff:put') OR hasAnyAuthority('user:put')")
    public void updateProfile(@RequestBody AccountUpdateDto accountUpdateDto){
        accountService.updateAccount(accountUpdateDto);
    }
}
