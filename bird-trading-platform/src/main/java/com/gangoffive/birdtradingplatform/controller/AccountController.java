package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.service.impl.AccountServiceImpl;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    @PutMapping("/shopowner/updateprofile")
//    @RolesAllowed({"SHOPOWNER"})
    @PreAuthorize("hasAnyAuthority('shopowner:update') OR hasAnyAuthority('shopstaff:update') OR hasAnyAuthority('user:update')")
    public void updateProfile(@RequestBody AccountUpdateDto accountUpdateDto){
        System.out.println("hello");
        accountService.updateAccount(accountUpdateDto);
    }
}
