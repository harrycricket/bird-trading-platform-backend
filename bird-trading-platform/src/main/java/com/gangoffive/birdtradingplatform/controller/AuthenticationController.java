package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.security.oauth2.AuthenticationRequest;
import com.gangoffive.birdtradingplatform.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    @PostMapping("/register")
    public ResponseEntity<String> register(
            @RequestBody AccountDto accountDto
    ) {
        return ResponseEntity.ok(authenticationService.register(accountDto));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(
            @RequestBody AuthenticationRequest request
    ) {
        return authenticationService.authenticate(request);
    }

    @GetMapping("/resetpassword")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        return ResponseEntity.ok(authenticationService.resetPassword(email));
    }
}
