package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.exception.AuthenticateException;
import com.gangoffive.birdtradingplatform.mapper.ShopOwnerMapper;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.service.InfoService;
import com.gangoffive.birdtradingplatform.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InfoServiceImpl implements InfoService {
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final ShopOwnerMapper shopOwnerMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
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
            String refreshToken = jwtService.generateRefreshToken(userPrincipal);
            account.get().setRefreshToken(refreshToken);
            accountRepository.save(account.get());
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
            List<Order> orders = orderRepository.findByShopOwner(shopOwner.get());
            List<OrderDetail> orderDetails = orderDetailRepository.findOrderDetailByOrderIn(orders);
            int totalProductOrder = orderDetails.stream().mapToInt(orderDetail -> orderDetail.getQuantity()).sum();
            ShopInfoDto shopInfoDto = shopOwnerMapper.modelToShopInfoDto(shopOwner.get());
            ShopSummaryDto shopSummaryDto = ShopSummaryDto.builder()
                    .shopInfoDto(shopInfoDto)
                    .totalProduct(productRepository.countAllByShopOwner_Id(id))
                    //rating lam sau
                    .rating("")
                    .totalProductOrder(totalProductOrder)
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
