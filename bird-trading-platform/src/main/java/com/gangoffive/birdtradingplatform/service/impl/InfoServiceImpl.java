package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.ProductStatusConstant;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.exception.AuthenticateException;
import com.gangoffive.birdtradingplatform.mapper.ShopOwnerMapper;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.service.InfoService;
import com.gangoffive.birdtradingplatform.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InfoServiceImpl implements InfoService {
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final ShopOwnerMapper shopOwnerMapper;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final JwtService jwtService;
    private final ShopStaffRepository shopStaffRepository;

    @Override
    public ResponseEntity<?> getUserInfo(String token) {
        if (token == null || token.isEmpty()) {
            throw new AuthenticateException("Not correct token to access");
        }
        String email;
        String staffUserName;
        try {
            email = jwtService.extractUsername(token);
            log.info("email {}", email);
            staffUserName = jwtService.extractStaffUsername(token);
        } catch (Exception ex) {
            throw new AuthenticateException("Can not extract token.");
        }
        if (!jwtService.isTokenExpired(token)) {
            if (staffUserName == null || staffUserName.isEmpty()) {
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
                        .role(account.get().getRole().ordinal() + 1)
                        .build();
                return ResponseEntity.ok().body(authenticationResponseDto);
            } else {
                Optional<ShopStaff> staffAccount = shopStaffRepository.findByUserName(staffUserName);
                TokenDto tokenDto = TokenDto.builder()
                        .accessToken(token)
                        .build();
                UserInfoDto userInfo = UserInfoDto.builder()
                        .id(staffAccount.get().getShopOwner().getId())
                        .email(staffAccount.get().getShopOwner().getAccount().getEmail())
                        .role(staffAccount.get().getShopOwner().getAccount().getRole())
                        .fullName(staffAccount.get().getUserName())
                        .shopName(staffAccount.get().getShopOwner().getShopName())
                        .phoneNumber(staffAccount.get().getShopOwner().getShopPhone())
                        .imgUrl(staffAccount.get().getShopOwner().getAvatarImgUrl())
                        .build();
                AuthenticationResponseDto authenticationResponseDto = AuthenticationResponseDto.builder()
                        .token(tokenDto)
                        .userInfo(userInfo)
                        .role(UserRole.SHOPSTAFF.ordinal() + 1)
                        .imgUrlStaff("https://bird-trading-platform.s3.ap-southeast-1.amazonaws.com/image/192aaae7-0692-443e-b363-490375a8f9f0.png")
                        .build();
                return ResponseEntity.ok().body(authenticationResponseDto);
            }

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
                    .totalProduct(productRepository.countAllByShopOwner_IdAndStatusIn(id, ProductStatusConstant.LIST_STATUS_GET_FOR_SHOP_OWNER))
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
