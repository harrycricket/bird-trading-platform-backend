package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.exception.AuthenticateException;
import com.gangoffive.birdtradingplatform.mapper.AddressMapper;
import com.gangoffive.birdtradingplatform.mapper.ShopOwnerMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import com.gangoffive.birdtradingplatform.repository.ShopOwnerRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.service.InfoService;
import com.gangoffive.birdtradingplatform.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final ShopOwnerMapper shopOwnerMapper;
    private final JwtService jwtService;

    @Override
    public ResponseEntity<?> getUserInfo(String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthenticateException("Not correct token to access");
        }
        String email;
        try {
            email = jwtService.extractUsername(token);
        } catch (Exception ex) {
            throw new AuthenticateException("Not correct token to access");
        }
        if (!jwtService.isTokenExpired(token)) {
            Optional<Account> account = accountRepository.findByEmail(email);
            UserPrincipal userPrincipal = UserPrincipal.create(account.get());
            String refreshToken = account.get().getRefreshToken();
            if (refreshToken != null) {
                if (jwtService.isTokenExpired(refreshToken)) {
                    refreshToken = jwtService.generateRefreshToken(userPrincipal);
                    account.get().setRefreshToken(refreshToken);
                    accountRepository.save(account.get());
                }
            } else {
                refreshToken = jwtService.generateRefreshToken(userPrincipal);
                account.get().setRefreshToken(refreshToken);
                accountRepository.save(account.get());
            }

            TokenDto tokenDto = TokenDto.builder()
                    .accessToken(token)
                    .refreshToken(refreshToken)
                    .build();
            UserInfoDto userInfo = UserInfoDto.builder()
                    .id(account.get().getId())
                    .email(account.get().getEmail())
                    .role(account.get().getRole())
                    .fullName(account.get().getFullName())
                    .phoneNumber(account.get().getPhoneNumber())
                    .imgUrl(account.get().getImgUrl())
                    .build();
            if (account.get().getAddress() != null) {
                userInfo.setAddress(account.get().getAddress().getAddress());
            }
            AuthenticationResponseDto authenticationResponseDto = AuthenticationResponseDto.builder()
                    .token(tokenDto)
                    .userInfo(userInfo)
                    .build();
            return ResponseEntity.ok().body(authenticationResponseDto);
        }
        throw new AuthenticateException("Not correct token to access");
    }

    @Override
    public ResponseEntity<?> getShopInfo(Long id) {
        Optional<ShopOwner> shopOwner = shopOwnerRepository.findById(id);
        if (shopOwner.isPresent()) {
            ShopInfoDto shopInfoDto = shopOwnerMapper.modelToShopInfoDto(shopOwner.get());
            ShopSummaryDto shopSummaryDto = ShopSummaryDto.builder()
                    .shopInfoDto(shopInfoDto)
                    .totalProduct(productRepository.countAllByShopOwner_Id(id))
                    //rating lam sau
                    .rating("")
                    .build();
            return ResponseEntity.ok(shopSummaryDto);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this shop.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
