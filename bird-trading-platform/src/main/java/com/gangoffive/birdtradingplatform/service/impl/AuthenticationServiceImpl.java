package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.AccountDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.VerifyToken;
import com.gangoffive.birdtradingplatform.enums.AuthProvider;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.VerifyTokenRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.security.oauth2.AuthenticationRequest;
import com.gangoffive.birdtradingplatform.security.oauth2.AuthenticationResponse;
import com.gangoffive.birdtradingplatform.service.AuthenticationService;
import com.gangoffive.birdtradingplatform.service.EmailSenderService;
import com.gangoffive.birdtradingplatform.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.temporal.TemporalUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final AccountMapper accountMapper;
    private final EmailSenderService emailSenderService;
    private final AppProperties appProperties;
    private final VerifyTokenRepository verifyTokenRepository;

    @Override
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

                //sending mail to verify
                String verificationCode = UUID.randomUUID().toString();
                String verificationLink = appProperties.getEmail().getVerifyLink() + verificationCode;
                log.info("verify link {}", verificationLink);
                String emailSubject = "Account Verification";
                String emailContent = "Dear User,\n\n"
                        + "Thank you for registering an account with our service. Please use the following verification code to activate your account:\n\n"
                        + "Verification Link <a href='http' > hehe </a>: " + verificationLink + "\n\n"
                        + "This link will expire after 10 minutes.\n\n"
                        + "If you did not create an account or have any questions, please contact our support team.\n\n"
                        + "Best regards,\n"
                        + "BirdStore2ND";
                VerifyToken verifyToken = new VerifyToken();
                verifyToken.setToken(verificationCode);
                verifyToken.setAccount(acc);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MINUTE,  10);
                Date expried = calendar.getTime();
                verifyToken.setExpired(expried);
                verifyToken.setRevoked(false);

                //save token
                verifyTokenRepository.save(verifyToken);

                //send mail
                emailSenderService.sendSimpleEmail(accountDto.getEmail(),emailContent,emailSubject);
                return "Register Successfully!";
            }else{
                return "The email has already been used!";
            }
        }
        return "Something went wrong!";

    }

    @Override
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
