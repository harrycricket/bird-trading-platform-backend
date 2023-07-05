package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Address;
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
import com.gangoffive.birdtradingplatform.service.EmailService;
import com.gangoffive.birdtradingplatform.service.JwtService;
import com.gangoffive.birdtradingplatform.util.MyUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final EmailService emailService;
    private final AppProperties appProperties;
    private final VerifyTokenRepository verifyTokenRepository;
    private final AddressMapper addressMapper;
    private final String emailSubject = "Reset Your Password";
    private final int expiration = 600000;


    @Override
    public ResponseEntity<?> register(AccountDto accountDto) {
        if (accountDto.getMatchingPassword().equals(accountDto.getPassword())) {
            Optional<Account> temp = accountRepository.findByEmail(accountDto.getEmail());
            if (!temp.isPresent()) {
                if (!emailService.isEmailExist(accountDto.getEmail())) {
                    return new ResponseEntity<>(ErrorResponse.builder()
                            .errorCode(HttpStatus.NOT_FOUND.name())
                            .errorMessage("The mail is not found!").build(), HttpStatus.NOT_FOUND);
                }
                return sendMailAndSaveAccount(accountDto);
            } else {
                if (temp.get().getStatus().equals(AccountStatus.VERIFY)) {
                    return new ResponseEntity<>(ErrorResponse.builder()
                            .errorCode(HttpStatus.CONFLICT.name())
                            .errorMessage("The email has already been used!").build(), HttpStatus.CONFLICT);
                } else {
                    return sendMailAndSaveAccount(accountDto);
                }
            }
        }
        return new ResponseEntity<>(ErrorResponse.builder()
                .errorCode(HttpStatus.BAD_REQUEST.name())
                .errorMessage("Something went wrong!").build(), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<?> sendMailAndSaveAccount(AccountDto accountDto) {
        Account acc = accountMapper.toModel(accountDto);
        acc.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        acc.setRole(UserRole.USER);
        acc.setStatus(AccountStatus.NOT_VERIFY);
        acc.setProvider(AuthProvider.local);

        //sending mail to verify
        int randomToken = MyUtils.generateSixRandomNumber();
        String emailSubject = "Account Verification";
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("Dear " + acc.getFullName() + ",\n");
        emailContent.append("Thank you for registering an account with our service. Please use the code to activate your account.\n");
        emailContent.append("Verification code: " + randomToken +"\n");
        emailContent.append("This link will expire after 10 minutes.\n");
        emailContent.append("If you did not create an account or have any questions, please contact our support team.\n");
        emailContent.append("Best regards,\n");
        emailContent.append("BirdStore2ND\n");

        VerifyToken verifyToken = new VerifyToken();
        verifyToken.setToken(randomToken);
        verifyToken.setAccount(acc);
        verifyToken.setRevoked(false);
        verifyToken.setExpired(new Date(System.currentTimeMillis() + expiration));
        verifyToken.setRevoked(false);
        //send mail
        try {
            emailService.sendSimpleEmail(accountDto.getEmail(), emailContent.toString(), emailSubject);
        } catch (Exception e) {
            return new ResponseEntity<>(ErrorResponse.builder()
                    .errorCode(HttpStatus.NOT_FOUND.name())
                    .errorMessage("The mail is not found!").build(), HttpStatus.NOT_FOUND);
        }
        accountRepository.save(acc);
        //save token
        verifyTokenRepository.save(verifyToken);
        return ResponseEntity.ok("Register Successfully!");
    }

    @Override
    public ResponseEntity<?> authenticate(AuthenticationRequestDto request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            ErrorResponse error = new ErrorResponse().builder()
                    .errorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()))
                    .errorMessage("Email or password not correct!")
                    .build();
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
//            throw new AuthenticateException("Email or password not correct!");
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
        return ResponseEntity.ok(getAuthenticationResponse(account, response));
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordDto resetPasswordDto) {
        Optional<Account> account = accountRepository.findByEmail(resetPasswordDto.getEmail());
        if (account.isPresent()) {
            if (
                    resetPasswordDto.getEmail() != null
                            && resetPasswordDto.getVerifyId() == null
                            && resetPasswordDto.getCode() == null
                            && resetPasswordDto.getNewPassword() == null
            ) {
                int randomToken = MyUtils.generateSixRandomNumber();
                VerifyToken verifyToken = new VerifyToken();
                verifyToken.setToken(randomToken);
                verifyToken.setExpired(new Date(System.currentTimeMillis() + expiration));
                verifyToken.setRevoked(false);
                verifyToken.setAccount(account.get());
                verifyTokenRepository.save(verifyToken);
                String emailContent = "Dear " + account.get().getFullName() + ",\n" +
                        "We received a request to reset your account password. Please use the code to reset your password.\n" +
                        "Code for reset password: " + randomToken + "\n" +
                        "This code will expire after 10 minutes.\n" +
                        "If you did not initiate this code or have any questions, please contact our support team.\n" +
                        "Best regards,\n" +
                        "BirdStore2ND\n";
                emailService.sendSimpleEmail(resetPasswordDto.getEmail(), emailContent, emailSubject);
                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.OK.value()))
                        .successMessage(MailSenderStatus.MAIL_SENT.name())
                        .build();
                return new ResponseEntity<>(successResponse, HttpStatus.OK);
            } else {
                Optional<VerifyToken> verifyToken = verifyTokenRepository.findByIdAndTokenAndAccount_IdAndRevokedIsTrue(
                        resetPasswordDto.getVerifyId(),
                        resetPasswordDto.getCode(),
                        account.get().getId()
                );
                log.info("verifyToken.isPresent() {}", verifyToken.isPresent());
                log.info("verifyToken.get().getExpired().before(new Date()) {}", verifyToken.get().getExpired().before(new Date()));
                if (verifyToken.isPresent() && verifyToken.get().getExpired().after(new Date())) {
                    account.get().setPassword(passwordEncoder.encode(resetPasswordDto.getNewPassword()));
                    accountRepository.save(account.get());
                    SuccessResponse successResponse = SuccessResponse.builder()
                            .successCode(String.valueOf(HttpStatus.OK.value()))
                            .successMessage("Reset password successfully.")
                            .build();
                    return new ResponseEntity<>(successResponse, HttpStatus.OK);
                } else {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                            .errorMessage("Something went wrong went reset password with your information.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
            }
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                    .errorMessage(MailSenderStatus.MAIL_NOT_FOUND.name())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    private AuthenticationResponseDto getAuthenticationResponse(Account account, HttpServletResponse response) {
        var jwtToken = jwtService.generateToken(UserPrincipal.create(account));
        var refreshToken = account.getRefreshToken();
        var addressDto = addressMapper.toDto(account.getAddress());
//        if (refreshToken != null && !refreshToken.isEmpty()) {
//            if (jwtService.isTokenExpired(refreshToken)) {
//                refreshToken = jwtService.generateRefreshToken(UserPrincipal.create(account));
//                account.setRefreshToken(refreshToken);
//                accountRepository.save(account);
//            }
//        } else {
            refreshToken = jwtService.generateRefreshToken(UserPrincipal.create(account));
            account.setRefreshToken(refreshToken);
            accountRepository.save(account);
//        }
        var tokenDto = TokenDto.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build();
        var userInfo = UserInfoDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .fullName(account.getFullName())
                .phoneNumber(account.getPhoneNumber())
                .imgUrl(account.getImgUrl())
                .address(Optional.ofNullable(account.getAddress()).map(Address::getAddress).orElse(""))
                .build();
        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        log.info("refreshTokenCookie: {}", refreshTokenCookie.getValue());
        refreshTokenCookie.setMaxAge(appProperties.getAuth().getRefreshTokenExpiration().intValue());
//        refreshTokenCookie.setDomain("localhost");
//        refreshTokenCookie.setDomain("birdstore2nd.vercel.app");
        refreshTokenCookie.setDomain("www.birdland2nd.store");
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setSecure(false);
        refreshTokenCookie.setHttpOnly(false);
        response.addCookie(refreshTokenCookie);
        return AuthenticationResponseDto.builder()
                .token(tokenDto)
                .userInfo(userInfo)
                .build();
    }
}
