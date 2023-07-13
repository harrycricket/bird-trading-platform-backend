package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.ShopStaff;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.mapper.ShopOwnerMapper;
import com.gangoffive.birdtradingplatform.repository.ShopStaffRepository;
import com.gangoffive.birdtradingplatform.security.UserPrincipal;
import com.gangoffive.birdtradingplatform.service.JwtService;
import com.gangoffive.birdtradingplatform.service.ShopOwnerService;
import com.gangoffive.birdtradingplatform.service.ShopStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShopStaffServiceImpl implements ShopStaffService {
    private final ShopStaffRepository shopStaffRepository;
    private final ShopOwnerService ownerService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ShopOwnerMapper shopOwnerMapper;

    @Override
    public ResponseEntity<?> updateStatus(ChangeStatusListIdDto updateStatusDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        long shopId = ownerService.getShopIdByEmail(email);
        if (updateStatusDto.getStatus() == -2 || updateStatusDto.getStatus() == 1) {
            AccountStatus accountStatus = AccountStatus.getAccountStatus(updateStatusDto.getStatus());
            int result = shopStaffRepository.updateStatusWithShopId(accountStatus, updateStatusDto.getIds(), shopId);
            if (updateStatusDto.getIds().size() == result) {
                return ResponseEntity.ok(SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.OK.value()))
                        .successMessage(String.format("Update %d staff successful", result))
                        .build());
            } else {
                return new ResponseEntity<>(ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .errorMessage(String.format("Update fail %d account", result)).build(),
                        HttpStatus.BAD_REQUEST);
            }
        }
        return new ResponseEntity<>(ErrorResponse.builder().errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value())
                )
                .errorMessage(String.format("Something went wrong")).build(),
                HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<?> authenticateStaffAccount(AuthenticationShopStaffRequest request) {
        Optional<ShopStaff> staff = shopStaffRepository.findByUserNameAndShopOwner_Id(request.getUsername(), request.getShopId());
        if (staff.isPresent()) {
            if (passwordEncoder.matches(request.getPassword(), staff.get().getPassword())) {
                Account account = new Account();
                account.setId(staff.get().getId());
                account.setEmail(staff.get().getUserName());
                account.setRole(UserRole.SHOPSTAFF);
                account.setPassword(staff.get().getShopOwner().getAccount().getPassword());
                String token = jwtService.generateTokenStaff(UserPrincipal.create(account), staff.get().getUserName());
                ShopInfoDto shopOwnerDto = shopOwnerMapper.modelToShopInfoDto(staff.get().getShopOwner());
                AuthenticationResponseDto authenticationResponseDto = AuthenticationResponseDto.builder()
                        .userInfo(shopOwnerDto)
                        .token(TokenDto.builder().accessToken(token).build())
                        .role(account.getRole().ordinal() + 1)
                        .build();
                return ResponseEntity.ok(authenticationResponseDto);
            } else {
                ErrorResponse error = ErrorResponse.builder()
                        .errorCode(HttpStatus.UNAUTHORIZED.toString())
                        .errorMessage("Email or password not correct!")
                        .build();
                return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
            }
        } else {
            ErrorResponse error = ErrorResponse.builder()
                    .errorCode(HttpStatus.UNAUTHORIZED.toString())
                    .errorMessage("Email or password not correct!")
                    .build();
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        }
    }
}
