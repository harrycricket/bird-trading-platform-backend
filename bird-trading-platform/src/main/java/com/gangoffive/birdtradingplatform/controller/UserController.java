package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.exception.ResourceNotFoundException;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.security.CurrentUser;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final AccountRepository accountRepository;

    @GetMapping("/user/me")
//    @PreAuthorize("hasRole('USER')")
    public Account getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return accountRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}
