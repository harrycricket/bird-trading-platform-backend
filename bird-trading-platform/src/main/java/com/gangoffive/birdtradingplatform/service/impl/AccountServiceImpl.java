package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ApiResponse;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.AccountUpdateDto;
import com.gangoffive.birdtradingplatform.dto.RegisterShopOwnerDto;
import com.gangoffive.birdtradingplatform.dto.UserInfoDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Address;
import com.gangoffive.birdtradingplatform.entity.Channel;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.AddressRepository;
import com.gangoffive.birdtradingplatform.repository.ShopOwnerRepository;
import com.gangoffive.birdtradingplatform.repository.VerifyTokenRepository;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.util.S3Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;
    private final VerifyTokenRepository verifyTokenRepository;
    private final AppProperties appProperties;
    private final ShopOwnerRepository shopOwnerRepository;

    @Override
    public ResponseEntity<?> updateAccount(AccountUpdateDto accountUpdateDto, MultipartFile multipartImage) {
        String originUrl = appProperties.getS3().getUrl();
        String urlImage = "";
        if (multipartImage != null && !multipartImage.isEmpty()) {
            String contentType = multipartImage.getContentType();
            log.info("contentType: {}", contentType);
            String newFilename = UUID.randomUUID().toString() + "." + contentType.substring(6);
            newFilename = "image/" + newFilename;
            log.info("newFilename update account: {}", newFilename);
            urlImage = originUrl + newFilename;
            try {
                S3Utils.uploadFile(newFilename, multipartImage.getInputStream());
            } catch (Exception ex) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .errorMessage("Upload file fail")
                        .build();
                new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        }
        Optional<Account> editAccount = accountRepository.findByEmail(accountUpdateDto.getEmail());
        editAccount.get().setFullName(accountUpdateDto.getFullName());
        editAccount.get().setPhoneNumber(accountUpdateDto.getPhoneNumber());
        if (multipartImage != null && !multipartImage.isEmpty()) {
            editAccount.get().setImgUrl(urlImage);
        }
        if (editAccount.get().getAddress() == null) {
            log.info("address null");
            log.info("editAccount.get().getAddress() == null {}", editAccount.get().getAddress().toString());
            Address address = new Address();
            address.setFullName(accountUpdateDto.getFullName());
            address.setPhone(accountUpdateDto.getPhoneNumber());
            address.setAddress(accountUpdateDto.getAddress());
            addressRepository.save(address);
            editAccount.get().setAddress(address);
        } else {
            log.info("editAccount.get().getAddress() {}", editAccount.get().getAddress().getAccount().getId());
            Address addressUpdate = editAccount.get().getAddress();
            addressUpdate.setFullName(accountUpdateDto.getFullName());
            addressUpdate.setPhone(accountUpdateDto.getPhoneNumber());
            addressUpdate.setAddress(accountUpdateDto.getAddress());
            addressRepository.save(addressUpdate);
        }
        Account updateAccount = accountRepository.save(editAccount.get());
        UserInfoDto userInfoDto = UserInfoDto.builder()
                .id(updateAccount.getId())
                .email(updateAccount.getEmail())
                .role(updateAccount.getRole())
                .fullName(updateAccount.getFullName())
                .phoneNumber(updateAccount.getPhoneNumber())
                .imgUrl(updateAccount.getImgUrl())
                .address(updateAccount.getAddress().getAddress())
                .build();
        return ResponseEntity.ok().body(userInfoDto);
    }

    @Override
    public ResponseEntity<?> registerShopOwnerAccount(RegisterShopOwnerDto registerShopOwnerDto, MultipartFile multipartImage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Optional<Account> account = accountRepository.findByEmail(username);
        if (account.get().getShopOwner() == null) {
            String originUrl = appProperties.getS3().getUrl();
            String urlImage = "";
            if (multipartImage != null && !multipartImage.isEmpty()) {
                String contentType = multipartImage.getContentType();
                log.info("contentType: {}", contentType);
                String newFilename = UUID.randomUUID().toString() + "." + contentType.substring(6);
                newFilename = "image/" + newFilename;
                log.info("newFilename update account: {}", newFilename);
                urlImage = originUrl + newFilename;
                try {
                    S3Utils.uploadFile(newFilename, multipartImage.getInputStream());
                } catch (Exception ex) {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                            .errorMessage("Upload file fail")
                            .build();
                    new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
            }
            ShopOwner shopOwner = ShopOwner.builder()
                    .account(account.get())
                    .shopName(registerShopOwnerDto.getShopName())
                    .shopPhone(registerShopOwnerDto.getPhoneShop())
                    .description(registerShopOwnerDto.getDescription())
                    .avatarImgUrl(urlImage)
                    .active(true)
                    .build();
            Address address = Address.builder()
                    .fullName(registerShopOwnerDto.getShopName())
                    .address(registerShopOwnerDto.getShopAddress())
                    .phone(registerShopOwnerDto.getPhoneShop())
                    .build();
            Address saveAddress = addressRepository.save(address);
            shopOwner.setAddress(saveAddress);
            shopOwnerRepository.save(shopOwner);
            account.get().setRole(UserRole.SHOPOWNER);
            accountRepository.save(account.get());
            SuccessResponse successResponse = SuccessResponse.builder()
                    .successCode(String.valueOf(HttpStatus.CREATED.value()))
                    .successMessage("Create shop owner account successfully.")
                    .build();
            return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.CONFLICT))
                    .errorMessage("Account already have shop account.")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<?> verifyToken(String token, boolean isResetPassword) {
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
                if (!isResetPassword) {
                    var account = tokenRepo.get().getAccount();
                    account.setStatus(AccountStatus.VERIFY);
                    accountRepository.save(account);
                }
                tokenRepo.get().setRevoked(true);
                verifyTokenRepository.save(tokenRepo.get());

                return ResponseEntity.ok(new ApiResponse(LocalDateTime.now(), "Verification of the account was successful!"));
            }
            ErrorResponse errorResponse = new ErrorResponse().builder().errorCode(HttpStatus.BAD_REQUEST.toString())
                    .errorMessage("This verify link has already used!").build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        ErrorResponse errorResponse = new ErrorResponse().builder().errorCode(HttpStatus.NOT_FOUND.toString())
                .errorMessage("Not found token. Link not true").build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @Override
    public long retrieveShopID(long accountId) {
        var acc = accountRepository.findById(accountId);
        if (acc.isPresent()) {
            ShopOwner shopOwner = acc.get().getShopOwner();
            if (shopOwner != null) {
                return shopOwner.getId();
            } else {
                throw new CustomRuntimeException("400", String.format("Cannot found shop with account id: %d", accountId));
            }
        } else {
            throw new CustomRuntimeException("400", String.format("Cannot found account with account id: %d", accountId));
        }
    }

    @Override
    @Transactional
    public List<Long> getAllChanelByUserId(long userId) {
        var acc = accountRepository.findById(userId);
        if (acc.isPresent()) {
            List<Channel> channels = acc.get().getChannels();
            if (channels != null || channels.size() != 0) {
                List<Long> listShopId = channels.stream().map(channel -> channel.getShopOwner().getId()).toList();
                return listShopId;
            } else {
                throw new CustomRuntimeException("400", "Cannot find channel");
            }
        } else {
            throw new CustomRuntimeException("400", String.format("Cannot find account with id %d", userId));
        }
    }
}
