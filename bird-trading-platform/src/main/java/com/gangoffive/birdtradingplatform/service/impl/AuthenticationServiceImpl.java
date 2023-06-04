package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.VerifyToken;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.enums.AuthProvider;
import com.gangoffive.birdtradingplatform.enums.MailSenderStatus;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.exception.AuthenticateException;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.mapper.AddressMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.VerifyTokenRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.service.AuthenticationService;
import com.gangoffive.birdtradingplatform.service.EmailSenderService;
import com.gangoffive.birdtradingplatform.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final AddressMapper addressMapper;
    private final String emailSubject = "Reset Your Password";
    private final int expiration = 600000;


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
        if (accountDto.getMatchingPassword().equals(accountDto.getPassword())) {
            Optional<Account> temp = accountRepository.findByEmail(accountDto.getEmail());
            if (!temp.isPresent()) {
                Account acc = accountMapper.toModel(accountDto);
                acc.setPassword(passwordEncoder.encode(accountDto.getPassword()));
                acc.setRole(UserRole.USER);
                acc.setStatus(AccountStatus.NOT_VERIFY);
                acc.setProvider(AuthProvider.local);

                //sending mail to verify
                String verificationCode = UUID.randomUUID().toString();
                String verificationLink = appProperties.getEmail().getVerifyLink() + "register?token=" + verificationCode;
                log.info("verify link {}", verificationLink);
                String emailSubject = "Account Verification";
                StringBuffer emailContent = new StringBuffer();
                emailContent.append("Dear User,");
                emailContent.append("Thank you for registering an account with our service. Please use the following verification code to activate your account:");
                emailContent.append("Verification: " + verificationLink);
                emailContent.append("This link will expire after 10 minutes.");
                emailContent.append("If you did not create an account or have any questions, please contact our support team.");
                emailContent.append("Best regards,");
                emailContent.append("BirdStore2ND");

                VerifyToken verifyToken = new VerifyToken();
                verifyToken.setToken(verificationCode);
                verifyToken.setAccount(acc);
                verifyToken.setRevoked(false);
                verifyToken.setExpired(new Date(System.currentTimeMillis() + expiration));
                verifyToken.setRevoked(false);
                //send mail
                try {
                    emailSenderService.sendSimpleEmail(accountDto.getEmail(), emailContent.toString(), emailSubject);
                } catch (Exception e) {
                    return "The mail is not correct!";
                }
                accountRepository.save(acc);
                //save token
                verifyTokenRepository.save(verifyToken);
                return "Register Successfully!";
            } else {
                return "The email has already been used!";
            }
        }
        return "Something went wrong!";
    }

    @Override
    public ResponseEntity<?> authenticate(AuthenticationRequestDto request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
//            ErrorResponse error = new ErrorResponse().builder()
//                    .errorCode(HttpStatus.UNAUTHORIZED.toString())
//                    .errorMessage("Email or password not correct!")
//                    .build();
//            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            throw new AuthenticateException("Email or password not correct!");
        }

        var account = accountRepository.findByEmail(request.getEmail()).orElse(null);
        if (account == null) {
            ErrorResponse error = ErrorResponse.builder()
                    .errorCode(HttpStatus.UNAUTHORIZED.toString())
                    .errorMessage("Email or password not correct!")
                    .build();
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
        if (account.getStatus().equals(AccountStatus.NOT_VERIFY)) {
            ErrorResponse error = ErrorResponse.builder()
                    .errorCode(HttpStatus.NOT_FOUND.toString())
                    .errorMessage("Email not found!")
                    .build();
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }
        if (account.getStatus().equals(AccountStatus.BANNED)) {
            ErrorResponse error = ErrorResponse.builder()
                    .errorCode(HttpStatus.LOCKED.toString())
                    .errorMessage("Email banned!")
                    .build();
            return new ResponseEntity<>(error, HttpStatus.LOCKED);
        }
        return ResponseEntity.ok(getAuthenticationResponse(account));
    }

    @Override
    public String resetPassword(String email) {
        Optional<Account> account = accountRepository.findByEmail(email);
        if (account.isPresent()) {
            String randomToken = UUID.randomUUID().toString();
            VerifyToken verifyToken = new VerifyToken();
            verifyToken.setToken(randomToken);
            verifyToken.setExpired(new Date(System.currentTimeMillis() + expiration));
            verifyToken.setRevoked(false);
            verifyToken.setAccount(account.get());
            verifyTokenRepository.save(verifyToken);
            String linkVerify = appProperties.getEmail().getVerifyLink() + "resetpassword?token=" + randomToken;
            StringBuffer emailContent = new StringBuffer();
            emailContent.append("Dear User,");
            emailContent.append("We received a request to reset your account password. Please click on the following link to proceed with the password reset process:");
            emailContent.append("Reset Password: " + linkVerify);
            emailContent.append("This link will expire after 10 minutes.");
            emailContent.append("If you did not initiate this request or have any questions, please contact our support team.");
            emailContent.append("Best regards,");
            emailContent.append("BirdStore2ND");
            emailSenderService.sendSimpleEmail(email, emailContent.toString(), emailSubject);
            return MailSenderStatus.MAIL_SENT.name();
        } else {
            return MailSenderStatus.MAIL_NOT_FOUND.name();
        }
    }

    private AuthenticationResponseDto getAuthenticationResponse(Account account) {
        var jwtToken = jwtService.generateToken(UserPrincipal.create(account));
        var refreshToken = jwtService.generateRefreshToken(UserPrincipal.create(account));
        var addressDto = addressMapper.toDto(account.getAddress());
        var tokenDto = TokenDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        var userInfo = UserInfoDto.builder()
                .email(account.getEmail())
                .role(account.getRole())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .imgUrl(account.getImgUrl())
                .address(addressDto)
                .build();
        return AuthenticationResponseDto.builder()
                .token(tokenDto)
                .userInfo(userInfo)
                .build();
    }
}
