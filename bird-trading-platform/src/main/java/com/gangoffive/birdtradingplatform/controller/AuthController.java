//package com.gangoffive.birdtradingplatform.controller;
//
//import com.gangoffive.birdtradingplatform.entity.Account;
//import com.gangoffive.birdtradingplatform.enums.AuthProvider;
//import com.gangoffive.birdtradingplatform.exception.BadRequestException;
//import com.gangoffive.birdtradingplatform.repository.AccountRepository;
//import com.gangoffive.birdtradingplatform.security.UserPrincipal;
//import com.gangoffive.birdtradingplatform.dto.AuthenticationRequest;
//import com.gangoffive.birdtradingplatform.dto.AuthenticationResponse;
//import com.gangoffive.birdtradingplatform.dto.RegisterRequest;
//import com.gangoffive.birdtradingplatform.service.JwtService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/auth")
//@RequiredArgsConstructor
//public class AuthController {
//    private final AuthenticationManager authenticationManager;
//
//    private final AccountRepository accountRepository;
//
//    private final PasswordEncoder passwordEncoder;
//
//    private final JwtService jwtService;
//
//    @PostMapping("/login")
//    public ResponseEntity<?> authenticateUser(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
//
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        authenticationRequest.getEmail(),
//                        authenticationRequest.getPassword()
//                )
//        );
//
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//
//        String token = jwtService.generateToken((UserPrincipal) authentication.getPrincipal());
//        String refreshToken = jwtService.generateRefreshToken((UserPrincipal) authentication.getPrincipal());
//        return ResponseEntity.ok(new AuthenticationResponse(token, refreshToken));
//    }
//
//    @PostMapping("/signup")
//    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
//        if(accountRepository.existsByEmail(registerRequest.getEmail())) {
//            throw new BadRequestException("Email address already in use.");
//        }
//
//        // Creating user's account
//        Account account = new Account();
//        account.setFullName(registerRequest.getFullName());
//        account.setEmail(registerRequest.getEmail());
//        account.setRole(registerRequest.getRole());
//        account.setAddress(registerRequest.getAddress());
//        account.setProvider(AuthProvider.local);
//
//        account.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
//        Account result = accountRepository.save(account);
//        String token = jwtService.generateToken(UserPrincipal.create(result));
//        String refreshToken = jwtService.generateRefreshToken(UserPrincipal.create(result));
//        return ResponseEntity.ok(new AuthenticationResponse(token, refreshToken));
//    }
//
//}
