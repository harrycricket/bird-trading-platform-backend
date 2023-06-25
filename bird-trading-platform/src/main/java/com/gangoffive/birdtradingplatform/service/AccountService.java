package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.dto.RegisterShopOwnerDto;
import com.gangoffive.birdtradingplatform.dto.VerifyRequestDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AccountService {
    ResponseEntity<?> updateAccount(AccountUpdateDto accountUpdateDto, MultipartFile multipartImage);

    ResponseEntity<?> registerShopOwnerAccount(RegisterShopOwnerDto registerShopOwnerDto, MultipartFile multipartImage);

    ResponseEntity<?> verifyToken(VerifyRequestDto verifyRequest, boolean isResetPassword);

    long retrieveShopID(long receiveId);
    public List<Long> getAllChanelByUserId (long userId);
}
