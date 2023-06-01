package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    @PutMapping("/users/updateprofile")
//    @RolesAllowed({"SHOPOWNER", "SHOPSTAFF", "USER"})
//    @PreAuthorize("hasAnyAuthority('shopowner:update', 'shopstaff:update', 'user:update')")\
    @PreAuthorize("hasAnyRole('SHOPOWNER', 'SHOPOWNER')")
    public void updateProfile(@RequestBody AccountUpdateDto accountUpdateDto){
        accountService.updateAccount(accountUpdateDto);
    }

    @GetMapping("/users/updateprofile")
//    @PreAuthorize("hasAnyAuthority('shopowner:update', 'shopstaff:update', 'user:update')")
    public String GetProfile(){
        return "GET";
    }

    @DeleteMapping("/users/updateprofile")
//    @PreAuthorize("hasAnyAuthority('shopowner:update', 'shopstaff:update', 'user:update')")
    public String DeleteProfile(){
        return "DELETE";
    }
}
