package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Address;
import com.gangoffive.birdtradingplatform.mapper.AccountMapper;
import com.gangoffive.birdtradingplatform.repository.AddressRepository;
import com.gangoffive.birdtradingplatform.service.AccountService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.gangoffive.birdtradingplatform.repository.AccountRepository;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final AddressRepository addressRepository;

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
}
