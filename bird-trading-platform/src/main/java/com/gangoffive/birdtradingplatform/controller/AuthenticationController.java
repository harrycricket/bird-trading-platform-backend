package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.dto.AuthenticationRequestDto;
import com.gangoffive.birdtradingplatform.service.AuthenticationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
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
            @RequestBody AuthenticationRequestDto request, HttpServletResponse response
    ) {
        return authenticationService.authenticate(request, response);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String email) {
        return ResponseEntity.ok(authenticationService.resetPassword(email));
    }

    @GetMapping("/get-cookie")
    public String authenticate(
            HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            // Iterate through the cookies and find the desired cookie
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    // Retrieve the value of the cookie
                    String cookieValue = cookie.getValue();
                    log.info("Cookie value: " + cookieValue);
                    return "Cookie value: " + cookieValue;
                }
            }
        }
        return "NO COOKIES";
    }

    @GetMapping("/test-token")
    public String testToken(){
        return "Token oke";
    }
}
