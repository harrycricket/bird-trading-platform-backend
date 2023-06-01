package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    @PutMapping("/users/updateprofile")
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

    @GetMapping("/users/verify")
    public ResponseEntity<?> verifyAccountRegister(@RequestParam String token){
        return accountService.verifyToken(token);
    }
}
