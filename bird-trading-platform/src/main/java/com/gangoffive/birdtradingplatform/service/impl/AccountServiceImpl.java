package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ApiResponse;
import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Address;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.repository.AddressRepository;
import com.gangoffive.birdtradingplatform.repository.VerifyTokenRepository;
import com.gangoffive.birdtradingplatform.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.gangoffive.birdtradingplatform.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AddressRepository addressRepository;
    private final VerifyTokenRepository verifyTokenRepository;

    @Override
    public Account updateAccount(AccountUpdateDto accountUpdateDto) {
        log.info("acc {}", accountUpdateDto.toString());
        Optional<Account> editAccount = accountRepository.findByEmail(accountUpdateDto.getEmail());
        editAccount.get().setFullName(accountUpdateDto.getFullName());
        editAccount.get().setPhoneNumber(accountUpdateDto.getPhoneNumber());
        if (editAccount.get().getAddress() == null) {
            Address address = new Address();
            address.setPhone(accountUpdateDto.getPhoneNumber());
            address.setStreet(accountUpdateDto.getStreet());
            address.setWard(accountUpdateDto.getWard());
            address.setDistrict(accountUpdateDto.getDistrict());
            address.setCity(accountUpdateDto.getCity());
            addressRepository.save(address);
            editAccount.get().setAddress(address);
        } else {
            Address addressUpdate = editAccount.get().getAddress();
            addressUpdate.setPhone(accountUpdateDto.getPhoneNumber());
            addressUpdate.setStreet(accountUpdateDto.getStreet());
            addressUpdate.setWard(accountUpdateDto.getWard());
            addressUpdate.setDistrict(accountUpdateDto.getDistrict());
            addressUpdate.setCity(accountUpdateDto.getCity());
            addressRepository.save(addressUpdate);
        }
        return accountRepository.save(editAccount.get());
    }

    @Override
    @Transactional
    public ResponseEntity<?> verifyToken(String token) {
        log.info("token {}", token);
        var tokenRepo = verifyTokenRepository.findByToken(token);
        if (tokenRepo.isPresent()) {
            if (!tokenRepo.get().isRevoked()) {
                Date expireDate = tokenRepo.get().getExpired();
                Date timeNow = new Date();
                if (timeNow.after(expireDate)) {
                    ErrorResponse errorResponse = new ErrorResponse().builder().errorCode(HttpStatus.BAD_REQUEST.toString())
                            .errorMessage("This link has already expired. Please regenerate the link to continue the verification").build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
                var account = tokenRepo.get().getAccount();
                //set revoked
                tokenRepo.get().setRevoked(true);
                account.setEnable(true);
                accountRepository.save(account);
                return ResponseEntity.ok(new ApiResponse(LocalDateTime.now(), "Verification of the account was successful!"));
            }

            ErrorResponse errorResponse = new ErrorResponse().builder().errorCode(HttpStatus.BAD_REQUEST.toString())
                    .errorMessage("This verify link has already used!").build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        ErrorResponse errorResponse = new ErrorResponse().builder().errorCode(HttpStatus.NOT_FOUND.toString())
                .errorMessage("Not found token. Link not true").build();

        verifyTokenRepository.save(tokenRepo.get());

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
