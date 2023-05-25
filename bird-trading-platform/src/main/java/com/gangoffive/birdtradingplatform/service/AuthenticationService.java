package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.enums.AuthProvider;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.security.oauth2.AuthenticationRequest;
import com.gangoffive.birdtradingplatform.security.oauth2.AuthenticationResponse;
import com.gangoffive.birdtradingplatform.security.oauth2.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountMapper accountMapper;
    public String register(AccountDto accountDto) {
//        var account = Account.builder()
//                .email(request.getEmail())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .firstName(request.getFirstname())
//                .lastName(request.getLastname())
//                .address(request.getAddress())
//                .role(request.getRole())
//                .build();
//        var saveAccount = accountRepository.save(account);
//        return getAuthenticationResponse(saveAccount);
        if (accountDto.getMatchingPassword().equals(accountDto.getPassword())){
            Optional<Account> temp = accountRepository.findByEmail(accountDto.getEmail());
            if(!temp.isPresent()) {
                Account acc = accountMapper.toModel(accountDto);
                acc.setPassword(passwordEncoder.encode(accountDto.getPassword()));
                acc.setRole(UserRole.USER);
                acc.setEnable(false);
                acc.setProvider(AuthProvider.local);
                accountRepository.save(acc);
                return "Register Successfully!";
            }else{
                return "The email has already been used!";
            }
        }
        return "Something went wrong!";

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var account = accountRepository.findByEmail(request.getEmail()).orElseThrow();
        return getAuthenticationResponse(account);
    }

    private AuthenticationResponse getAuthenticationResponse(Account account) {
        var jwtToken = jwtService.generateToken(UserPrincipal.create(account));
        var refreshToken = jwtService.generateRefreshToken(UserPrincipal.create(account));
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
    }
}
