package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.*;
import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.AddressRepository;
import com.gangoffive.birdtradingplatform.repository.ShopOwnerRepository;
import com.gangoffive.birdtradingplatform.repository.VerifyTokenRepository;
import com.gangoffive.birdtradingplatform.service.AccountService;
import com.gangoffive.birdtradingplatform.util.*;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

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
        //remember fix when authentication
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        String email = authentication.getName();
        String originUrl = appProperties.getS3().getUrl();
        String urlImage = "";
        if (multipartImage != null && !multipartImage.isEmpty()) {
            String newFileName = FileNameUtils.getNewImageFileName(multipartImage);
            urlImage = originUrl + newFileName;
            try {
                S3Utils.uploadFile(newFileName, multipartImage.getInputStream());
            } catch (Exception ex) {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .errorMessage("Upload file fail")
                        .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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
            Address address = new Address();
            address.setFullName(accountUpdateDto.getFullName());
            address.setPhone(accountUpdateDto.getPhoneNumber());
            address.setAddress(accountUpdateDto.getAddress());
            Address saveAddress = addressRepository.save(address);
            editAccount.get().setAddress(saveAddress);
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
                String newFileName = FileNameUtils.getNewImageFileName(multipartImage);
                urlImage = originUrl + newFileName;
                try {
                    S3Utils.uploadFile(newFileName, multipartImage.getInputStream());
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
                    .status(ShopOwnerStatus.ACTIVE)
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
    public ResponseEntity<?> verifyToken(VerifyRequestDto verifyRequest, boolean isResetPassword) {
        log.info("token {}", verifyRequest.getCode());
        Optional<Account> account = accountRepository.findByEmail(verifyRequest.getEmail());
        if (!account.isPresent()) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(HttpStatus.NOT_FOUND.toString())
                    .errorMessage("Not correct email.").build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        var tokenRepo = verifyTokenRepository.findByTokenAndAccount_Id(verifyRequest.getCode(), account.get().getId());
        if (tokenRepo.isPresent()) {
            if (!tokenRepo.get().isRevoked()) {
                Date expireDate = tokenRepo.get().getExpired();
                Date timeNow = new Date();
                if (timeNow.after(expireDate)) {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(HttpStatus.BAD_REQUEST.toString())
                            .errorMessage("This code has already expired. Please regenerate the code to continue the verification")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
                if (!isResetPassword) {
                    account.get().setStatus(AccountStatus.VERIFY);
                    accountRepository.save(account.get());
                }
                tokenRepo.get().setRevoked(true);
                VerifyToken saveVerifyToken = verifyTokenRepository.save(tokenRepo.get());
                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.OK.value()))
                        .successMessage("Verification of the account was successful. Id: " + saveVerifyToken.getId())
                        .build();

                return new ResponseEntity<>(successResponse, HttpStatus.OK);
            }
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(HttpStatus.BAD_REQUEST.toString())
                    .errorMessage("This verify code has already used!").build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(HttpStatus.NOT_FOUND.toString())
                .errorMessage("Not found code. Code not true")
                .build();
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

    @Override
    public Account getAccountById(long userId) {
        var acc = accountRepository.findById(userId);
        if (acc.isPresent()) {
            return acc.get();
        } else {
            throw new CustomRuntimeException("400", "Not found this account Id");
        }
    }

    @Override
    public ResponseEntity<?> filterAllUserAccount(UserAccountFilterDto userAccountFilter) {
        if (userAccountFilter.getPageNumber() > 0) {
            int pageNumber = userAccountFilter.getPageNumber() - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
            PageRequest pageRequestWithSort = null;
            if (userAccountFilter.getSortDirection() != null
                    && !userAccountFilter.getSortDirection().getSort().isEmpty()
                    && !userAccountFilter.getSortDirection().getField().isEmpty()
            ) {
                if (
                        !SortUserAccountColumn.checkField(userAccountFilter.getSortDirection().getField())
                ) {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                            .errorMessage("Not found this field in sort direction.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
                if (userAccountFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
                    pageRequestWithSort = getPageRequest(userAccountFilter, pageNumber, Sort.Direction.ASC);
                } else if (userAccountFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
                    pageRequestWithSort = getPageRequest(userAccountFilter, pageNumber, Sort.Direction.DESC);
                } else {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                            .errorMessage("Not found this direction.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
            }

            if (
                    userAccountFilter.getUserSearchInfo().getField().isEmpty()
                            && userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getUserSearchInfo().getOperator().isEmpty()
                            && userAccountFilter.getSortDirection().getField().isEmpty()
                            && userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                return filterAllUserAccountAllFieldEmpty(pageRequest);
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().isEmpty()
                            && userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getUserSearchInfo().getOperator().isEmpty()
                            && !userAccountFilter.getSortDirection().getField().isEmpty()
                            && !userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                return filterAllUserAccountAllFieldEmpty(pageRequestWithSort);
            }

            if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.ID.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterUserAccountByIdEqual(userAccountFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.EMAIL.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getSortDirection().getField().isEmpty()
                            && userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByEmailContain(userAccountFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.EMAIL.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByEmailContain(userAccountFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.FULL_NAME.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getSortDirection().getField().isEmpty()
                            && userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByFullNameContain(userAccountFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.FULL_NAME.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByFullNameContain(userAccountFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.PHONE_NUMBER.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getSortDirection().getField().isEmpty()
                            && userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByPhoneNumberContain(userAccountFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.PHONE_NUMBER.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByPhoneNumberContain(userAccountFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.ADDRESS.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getSortDirection().getField().isEmpty()
                            && userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByAddressContain(userAccountFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.ADDRESS.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterUserAccountByAddressContain(userAccountFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.STATUS.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getSortDirection().getField().isEmpty()
                            && userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterUserAccountByStatusEqual(userAccountFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.STATUS.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterUserAccountByStatusEqual(userAccountFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.CREATED_DATE.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
                            && userAccountFilter.getSortDirection().getField().isEmpty()
                            && userAccountFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(userAccountFilter.getUserSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterUserAccountByCreatedDateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterUserAccountByCreatedDateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    userAccountFilter.getUserSearchInfo().getField().equals(FieldUserAccountTable.CREATED_DATE.getField())
                            && !userAccountFilter.getUserSearchInfo().getValue().isEmpty()
            ) {
                if (userAccountFilter.getUserSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(userAccountFilter.getUserSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterUserAccountByCreatedDateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterUserAccountByCreatedDateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            }

            return null;
        } else {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                    "Page number cannot less than 1");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> filterUserAccountByCreatedDateFromTo(
            DateRangeDto dateRange, PageRequest pageRequest
    ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<Account>> accounts = accountRepository.findByCreatedDateBetween(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found account have created date from to.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterUserAccountByCreatedDateGreaterThanOrEqual(
            DateRangeDto dateRange, PageRequest pageRequest
    ) {
        Optional<Page<Account>> accounts = accountRepository.findByCreatedDateGreaterThanEqual(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found account have created date greater than or equal.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterUserAccountByStatusEqual(
            UserAccountFilterDto userAccountFilter, PageRequest pageRequest
    ) {
        List<AccountStatus> accountStatuses;
        if (Integer.parseInt(userAccountFilter.getUserSearchInfo().getValue()) == 9) {
            accountStatuses = List.of(AccountStatus.values());
        } else {
            accountStatuses = Arrays.asList(
                    AccountStatus.getAccountStatus(
                            Integer.parseInt(userAccountFilter.getUserSearchInfo().getValue())
                    )
            );
        }

        Optional<Page<Account>> accounts = accountRepository.findByStatusIn(
                accountStatuses,
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this status.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterUserAccountByAddressContain(
            UserAccountFilterDto userAccountFilter, PageRequest pageRequest
    ) {
        Optional<Page<Account>> accounts = accountRepository.findByAddress_AddressLike(
                "%" + userAccountFilter.getUserSearchInfo().getValue() + "%",
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found account have contain this address.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterUserAccountByPhoneNumberContain(
            UserAccountFilterDto userAccountFilter, PageRequest pageRequest
    ) {
        Optional<Page<Account>> accounts = accountRepository.findByPhoneNumberLike(
                "%" + userAccountFilter.getUserSearchInfo().getValue() + "%",
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found account have contain this phone number.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterUserAccountByFullNameContain(
            UserAccountFilterDto userAccountFilter, PageRequest pageRequest
    ) {
        Optional<Page<Account>> accounts = accountRepository.findByFullNameLike(
                "%" + userAccountFilter.getUserSearchInfo().getValue() + "%",
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found account have contain this full name.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterUserAccountByEmailContain(
            UserAccountFilterDto userAccountFilter, PageRequest pageRequest
    ) {
        Optional<Page<Account>> accounts = accountRepository.findByEmailLike(
                "%" + userAccountFilter.getUserSearchInfo().getValue() + "%",
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found account have contain this email.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterUserAccountByIdEqual(
            UserAccountFilterDto userAccountFilter, PageRequest pageRequest
    ) {
        Optional<Page<Account>> accounts = accountRepository.findById(
                Long.valueOf(userAccountFilter.getUserSearchInfo().getValue()),
                pageRequest
        );

        if (accounts.isPresent()) {
            return getPageNumberWrapperWithUserAccount(accounts.get());
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this account id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterAllUserAccountAllFieldEmpty(PageRequest pageRequest) {
        Page<Account> accounts = accountRepository.findAll(
                pageRequest
        );

        if (!accounts.isEmpty()) {
            return getPageNumberWrapperWithUserAccount(accounts);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                    .errorMessage("Not found account.")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> getPageNumberWrapperWithUserAccount(Page<Account> accounts) {
        List<UserAccountDto> userAccounts = accounts.stream()
                .map(this::accountToUserAccountDto)
                .collect(Collectors.toList());
        PageNumberWrapper<UserAccountDto> result = new PageNumberWrapper<>(
                userAccounts,
                accounts.getTotalPages(),
                accounts.getTotalElements()
        );
        return ResponseEntity.ok(result);

    }

    private PageRequest getPageRequest(
            UserAccountFilterDto userAccountFilter, int pageNumber, Sort.Direction sortDirection
    ) {
        return PageRequest.of(
                pageNumber,
                PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                Sort.by(sortDirection,
                        SortUserAccountColumn.getColumnByField(userAccountFilter.getSortDirection().getField())
                )
        );
    }

    private UserAccountDto accountToUserAccountDto(Account account) {
        UserAccountDto userAccount = UserAccountDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .status(account.getStatus())
                .createdDate(account.getCreatedDate().getTime())
                .build();
        if (account.getFullName() != null) {
            userAccount.setFullName(account.getFullName());
        }
        if (account.getPhoneNumber() != null) {
            userAccount.setPhoneNumber(account.getPhoneNumber());
        }
        if (account.getAddress() != null) {
            userAccount.setAddress(account.getAddress().getAddress());
        }
        return userAccount;
    }
}
