package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;

public interface AccountService {
    Account updateAccount(AccountUpdateDto accountUpdateDto);
}
