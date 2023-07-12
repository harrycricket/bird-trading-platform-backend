package com.gangoffive.birdtradingplatform.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.common.NotifiConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.config.AppProperties;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.Currency;
import com.gangoffive.birdtradingplatform.enums.*;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.*;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PackageOrderServiceImpl implements PackageOrderService {
    private final AppProperties appProperties;
    private final ProductRepository productRepository;
    private final PromotionRepository promotionRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductService productService;
    private final AccountRepository accountRepository;
    private final PackageOrderRepository packageOrderRepository;
    private final TransactionRepository transactionRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final PaypalService paypalService;
    private final PromotionPriceService promotionPriceService;
    private final NotificationService notificationService;
    private final ProductSummaryService productSummaryService;

    @Override
    @Transactional
    public ResponseEntity<?> packageOrder(PackageOrderRequestDto packageOrder, String paymentId, String payerId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        log.info("username: {}", username);
        Optional<Account> account = accountRepository.findByEmail(username);
        Map<Long, Integer> productWithQuantityMap = getAllProductWithQuantity(packageOrder.getCartInfo().getItemsByShop());
        if (paymentId != null && payerId != null) {
            return handleSuccessPayment(packageOrder, productWithQuantityMap, account.get(), paymentId, payerId);
        }

        if (checkUserOrderDto(packageOrder.getUserInfo())) {
            if (checkListProduct(productWithQuantityMap)) {
                if (checkPromotion(packageOrder, productWithQuantityMap)) {
                    if (checkTotalShopPrice(packageOrder.getCartInfo().getItemsByShop())) {
                        if (checkSubTotal(packageOrder.getCartInfo().getTotal().getSubTotal(), productWithQuantityMap)) {
                            if (checkTotalShippingFee(packageOrder)) {
                                if (checkTotalDiscount(packageOrder)) {
                                    if (checkTotalPayment(packageOrder.getCartInfo().getTotal())) {
                                        PaymentMethod paymentMethod = packageOrder.getCartInfo().getPaymentMethod();
                                        if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
                                            return handleInitialPayment(packageOrder, account.get());
                                        } else if (paymentMethod.equals(PaymentMethod.DELIVERY)) {
                                            Long packageOrderId = saveAll(packageOrder, paymentId, null, account.get(), productWithQuantityMap);
                                            updateTotalOrderOfListProduct(productWithQuantityMap.keySet().stream().toList());
                                            SuccessResponse successResponse = SuccessResponse.builder()
                                                    .successCode(String.valueOf(HttpStatus.OK.value()))
                                                    .successMessage("Order successfully. packageOrderId=" + packageOrderId)
                                                    .build();
                                            return new ResponseEntity<>(successResponse, HttpStatus.OK);
                                        } else {
                                            return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong with payment method");
                                        }
                                    } else {
                                        return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong in total payment.");
                                    }
                                } else {
                                    return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong in total discount.");
                                }
                            } else {
                                return ResponseUtils.getErrorResponseNotAcceptable("Shipping not support this location.");
                            }
                        } else {
                            return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong in subtotal order.");
                        }
                    } else {
                        return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong in total shop price.");
                    }
                } else {
                    return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong in list promotions.");
                }
            } else {
                return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong in list product(Out of stock).");
            }
        } else {
            return ResponseUtils.getErrorResponseNotAcceptable("Something went wrong in your info.");
        }

//        if (
//                checkUserOrderDto(packageOrder.getUserInfo())
//                        && checkListProduct(productWithQuantityMap)
//                        && checkPromotion(packageOrder, productWithQuantityMap)
//                        && checkTotalShopPrice(packageOrder.getCartInfo().getItemsByShop())
//                        && checkSubTotal(packageOrder.getCartInfo().getTotal().getSubTotal(), productWithQuantityMap)
//                        && checkTotalShippingFee(packageOrder)
//                        && checkTotalDiscount(packageOrder)
//                        && checkTotalPayment(packageOrder.getCartInfo().getTotal())
//        ) {
//            PaymentMethod paymentMethod = packageOrder.getCartInfo().getPaymentMethod();
//            if (paymentMethod.equals(PaymentMethod.PAYPAL)) {
//                return handleInitialPayment(packageOrder, account.get());
//            } else if (paymentMethod.equals(PaymentMethod.DELIVERY)) {
//                Long packageOrderId = saveAll(packageOrder, paymentId, account.get(), productWithQuantityMap);
//                SuccessResponse successResponse = SuccessResponse.builder()
//                        .successCode(String.valueOf(HttpStatus.OK.value()))
//                        .successMessage("Order successfully. packageOrderId=" + packageOrderId)
//                        .build();
//                return new ResponseEntity<>(successResponse, HttpStatus.OK);
//            } else {
//                ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.NOT_FOUND.value()),
//                        "Something went wrong");
//                return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
//            }
//        } else {
//            ErrorResponse error = new ErrorResponse(
//                    String.valueOf(HttpStatus.NOT_ACCEPTABLE.value()),
//                    "Something went wrong");
//            log.info("here");
//            return new ResponseEntity<>(error, HttpStatus.NOT_ACCEPTABLE);
//        }
    }

    @Override
    public ResponseEntity<?> viewAllPackageOrderByAccountId(int pageNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Account> account = accountRepository.findByEmail(email);
        log.info("account id {}", account.get().getId());
        if (account.isPresent()) {
            if (pageNumber > 0) {
                pageNumber = pageNumber - 1;
                PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SIZE,
                        Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "lastedUpdate"));
                Optional<Page<PackageOrder>> pageAble = packageOrderRepository.findAllByAccount(account.get(), pageRequest);
                pageAble.get().stream().forEach(packageOrder -> log.info("pack {}", packageOrder.getId()));
                if (pageAble.isPresent()) {
                    List<PackageOrderDto> packageOrderDtoList = pageAble.get().stream()
                            .map(this::packageOrderToPackageOrderDto)
                            .toList();
                    PageNumberWrapper<PackageOrderDto> pageNumberWrapper = new PageNumberWrapper<>(packageOrderDtoList, pageAble.get().getTotalPages());
                    return ResponseEntity.ok(pageNumberWrapper);
                } else {
                    return ResponseUtils.getErrorResponseNotFound("Not found package order in this account.");
                }
            } else {
                return ResponseUtils.getErrorResponseBadRequestPageNumber();
            }
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found this account.");
        }
    }

    @Override
    public ResponseEntity<?> filterAllPackageOrder(PackageOrderAdminFilterDto packageOrderFilter) {
        if (packageOrderFilter.getPageNumber() > 0) {
            int pageNumber = packageOrderFilter.getPageNumber() - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
            PageRequest pageRequestWithSort = null;
            if (packageOrderFilter.getSortDirection() != null
                    && !packageOrderFilter.getSortDirection().getSort().isEmpty()
                    && !packageOrderFilter.getSortDirection().getField().isEmpty()
            ) {
                if (
                        !SortPackageOrderAdminColumn.checkField(packageOrderFilter.getSortDirection().getField())
                ) {
                    return ResponseUtils.getErrorResponseNotFoundSortColumn();
                }
                if (packageOrderFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
                    pageRequestWithSort = getPageRequest(packageOrderFilter, pageNumber, Sort.Direction.ASC);
                } else if (packageOrderFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
                    pageRequestWithSort = getPageRequest(packageOrderFilter, pageNumber, Sort.Direction.DESC);
                } else {
                    return ResponseUtils.getErrorResponseNotFoundSortDirection();
                }
            }

            if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().isEmpty()
                            && packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getPackageOrderSearchInfo().getOperator().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                return filterAllPackageOrderAllFieldEmpty(pageRequest);
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().isEmpty()
                            && packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getPackageOrderSearchInfo().getOperator().isEmpty()
                            && !packageOrderFilter.getSortDirection().getField().isEmpty()
                            && !packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                return filterAllPackageOrderAllFieldEmpty(pageRequestWithSort);
            }

            if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.ID.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPackageOrderByIdEqual(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.ACCOUNT_ID.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPackageOrderByAccountIdEqual(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.ACCOUNT_ID.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPackageOrderByAccountIdEqual(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.PAYMENT_METHOD.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPackageOrderByPaymentMethodEqual(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.PAYMENT_METHOD.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPackageOrderByPaymentMethodEqual(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.PAYER_EMAIL.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByPayerEmailContain(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.PAYER_EMAIL.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByPayerEmailContain(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.TRANSACTION_STATUS.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPackageOrderByTransactionStatusEqual(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.TRANSACTION_STATUS.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterPackageOrderByTransactionStatusEqual(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.TOTAL_PAYMENT.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPackageOrderByTotalPaymentGreaterThanOrEqual(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.TOTAL_PAYMENT.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterPackageOrderByTotalPaymentGreaterThanOrEqual(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.CREATED_DATE_TRANSACTION.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(packageOrderFilter.getPackageOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPackageOrderByCreatedDateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterPackageOrderByCreatedDateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.CREATED_DATE_TRANSACTION.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(packageOrderFilter.getPackageOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPackageOrderByCreatedDateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterPackageOrderByCreatedDateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.LASTED_UPDATE_TRANSACTION.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(packageOrderFilter.getPackageOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPackageOrderByLastedUpdateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterPackageOrderByLastedUpdateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.LASTED_UPDATE_TRANSACTION.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(packageOrderFilter.getPackageOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterPackageOrderByLastedUpdateGreaterThanOrEqual(dateRange, pageRequest);
                    } else {
                        return filterPackageOrderByLastedUpdateFromTo(dateRange, pageRequest);
                    }
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.FULL_NAME.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByFullNameContain(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.FULL_NAME.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByFullNameContain(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.PHONE_NUMBER.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByPhoneNumberContain(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.PHONE_NUMBER.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByPhoneNumberContain(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.ADDRESS.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
                            && packageOrderFilter.getSortDirection().getField().isEmpty()
                            && packageOrderFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByAddressContain(packageOrderFilter, pageRequest);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else if (
                    packageOrderFilter.getPackageOrderSearchInfo().getField().equals(FieldPackageOrderAdminTable.ADDRESS.getField())
                            && !packageOrderFilter.getPackageOrderSearchInfo().getValue().isEmpty()
            ) {
                if (packageOrderFilter.getPackageOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterPackageOrderByAddressContain(packageOrderFilter, pageRequestWithSort);
                }
                return ResponseUtils.getErrorResponseNotFoundOperator();
            } else {
                return ResponseUtils.getErrorResponseBadRequest("Package order filter is not correct.");
            }
        } else {
            return ResponseUtils.getErrorResponseBadRequestPageNumber();
        }
    }

    private ResponseEntity<?> filterPackageOrderByAddressContain(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest
    ) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByShippingAddress_AddressLike(
                "%" + packageOrderFilter.getPackageOrderSearchInfo().getValue() + "%",
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this address.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByPhoneNumberContain(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest
    ) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByShippingAddress_PhoneLike(
                "%" + packageOrderFilter.getPackageOrderSearchInfo().getValue() + "%",
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this phone number.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByFullNameContain(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByShippingAddress_FullNameLike(
                "%" + packageOrderFilter.getPackageOrderSearchInfo().getValue() + "%",
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this full name.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByLastedUpdateFromTo(DateRangeDto dateRange, PageRequest pageRequest) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByTransaction_LastedUpdateBetween(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this lasted update date from to.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByLastedUpdateGreaterThanOrEqual(
            DateRangeDto dateRange, PageRequest pageRequest) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByTransaction_LastedUpdateGreaterThanEqual(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this lasted update date from.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByCreatedDateFromTo(DateRangeDto dateRange, PageRequest pageRequest) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByTransaction_TransactionDateBetween(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this created date from to.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByCreatedDateGreaterThanOrEqual(
            DateRangeDto dateRange, PageRequest pageRequest
    ) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByTransaction_TransactionDateGreaterThanEqual(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this created date from.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByTotalPaymentGreaterThanOrEqual(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest
    ) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByTotalPriceGreaterThanEqual(
                Double.parseDouble(packageOrderFilter.getPackageOrderSearchInfo().getValue()),
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this total payment.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByTransactionStatusEqual(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest) {
        List<TransactionStatus> transactionStatuses;
        if (Integer.parseInt(packageOrderFilter.getPackageOrderSearchInfo().getValue()) == 9) {
            transactionStatuses = List.of(TransactionStatus.values());
        } else {
            transactionStatuses = Arrays.asList(
                    TransactionStatus.getTransactionStatusByValue(
                            Integer.parseInt(packageOrderFilter.getPackageOrderSearchInfo().getValue())
                    )
            );
        }
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByTransaction_StatusIn(
                transactionStatuses, pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this transaction status.");
        }

    }

    private ResponseEntity<?> filterPackageOrderByPayerEmailContain(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest
    ) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByTransaction_PaypalEmailLike(
                "%" + packageOrderFilter.getPackageOrderSearchInfo().getValue() + "%",
                pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this email.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByPaymentMethodEqual(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest
    ) {
        List<PaymentMethod> paymentMethods;
        if (packageOrderFilter.getPackageOrderSearchInfo().getValue().trim().equals("9")) {
            paymentMethods = List.of(PaymentMethod.values());
        } else {
            paymentMethods = List.of(PaymentMethod.valueOf(packageOrderFilter.getPackageOrderSearchInfo().getValue()));
        }
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByPaymentMethodIn(
                paymentMethods, pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this payment method.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByAccountIdEqual(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest
    ) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findAllByAccount_Id(
                Long.valueOf(packageOrderFilter.getPackageOrderSearchInfo().getValue()), pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order with this account id.");
        }
    }

    private ResponseEntity<?> filterPackageOrderByIdEqual(
            PackageOrderAdminFilterDto packageOrderFilter, PageRequest pageRequest) {
        Optional<Page<PackageOrder>> packageOrders = packageOrderRepository.findById(
                Long.valueOf(packageOrderFilter.getPackageOrderSearchInfo().getValue()), pageRequest
        );
        if (packageOrders.isPresent()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders.get());
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found this package order id.");
        }
    }

    private ResponseEntity<?> filterAllPackageOrderAllFieldEmpty(PageRequest pageRequest) {
        Page<PackageOrder> packageOrders = packageOrderRepository.findAll(
                pageRequest
        );
        if (!packageOrders.isEmpty()) {
            return getPageNumberWrapperWithPackageOrder(packageOrders);
        } else {
            return ResponseUtils.getErrorResponseNotFound("Not found package order.");
        }
    }

    private ResponseEntity<?> getPageNumberWrapperWithPackageOrder(Page<PackageOrder> packageOrders) {
        List<PackageOrderAdminDto> packageOrdersAdmin = packageOrders.stream()
                .map(this::packageOrderToPackageOrderAdminDto)
                .toList();
        PageNumberWrapper<PackageOrderAdminDto> result = new PageNumberWrapper<>(
                packageOrdersAdmin,
                packageOrders.getTotalPages(),
                packageOrders.getTotalElements()
        );
        return ResponseEntity.ok(result);
    }

    private PageRequest getPageRequest(
            PackageOrderAdminFilterDto packageOrderFilter, int pageNumber, Sort.Direction sortDirection
    ) {
        return PageRequest.of(
                pageNumber,
                PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                Sort.by(sortDirection,
                        SortPackageOrderAdminColumn.getColumnByField(packageOrderFilter.getSortDirection().getField())
                )
        );
    }

    public boolean checkUserOrderDto(UserOrderDto userOrderDto) {
        return !(userOrderDto.getFullName() == null || userOrderDto.getFullName().isEmpty())
                && !(userOrderDto.getPhoneNumber() == null || userOrderDto.getPhoneNumber().isEmpty())
                && !(userOrderDto.getAddress() == null || userOrderDto.getAddress().isEmpty());
    }

    public boolean checkListProduct(Map<Long, Integer> productOrder) {
//        for (Long id : productOrder.keySet()) {
//            if (!productRepository.findById(id).isPresent()) {
//                return false;
//            } else {
//                if (productRepository.findById(id).get().getQuantity() >= productOrder.get(id)) {
//                    return false;
//                }
//            }
//        }
//        return true;
        if (productOrder == null || productOrder.isEmpty()) {
            return false;
        }
        return productOrder.keySet()
                .stream()
                .allMatch(
                        productId -> {
                            Optional<Product> productOptional = productRepository.findById(productId);
                            if (!productOptional.isPresent()) {
                                return false;
                            } else {
                                Product product = productOptional.get();
                                return product.getQuantity() >= productOrder.get(productId);
                            }
                        }
                );
    }

    //Check all condition can use promotion
    public boolean checkPromotion(PackageOrderRequestDto packageOrder, Map<Long, Integer> productOrder) {
        //Return true when don't have promotion
        if (packageOrder.getCartInfo().getPromotionIds() == null || packageOrder.getCartInfo().getPromotionIds().isEmpty()) {
            return true;
        }

        //Return false when don't have product
        if (productOrder == null || productOrder.isEmpty()) {
            return false;
        }

        //Check list promotion
        List<Promotion> listPromotion = promotionRepository.findAllById(packageOrder.getCartInfo().getPromotionIds());
        if (listPromotion.size() != packageOrder.getCartInfo().getPromotionIds().size()) {
            return false;
        }
        if (listPromotion.size() > 2) {
            return false;
        }

        //Check used promotion
        boolean checkUsed = listPromotion.stream().allMatch(promotion -> promotion.getUsed() < promotion.getUsageLimit());
        if (!checkUsed) {
            return false;
        }

        //Check end date of promotion
        ZoneId databaseTimeZone = ZoneId.of("Asia/Bangkok");
        LocalDateTime currentDate = new Date().toInstant().atZone(databaseTimeZone).toLocalDateTime();
        //log.info("currentDate {}", currentDate);
        for (Promotion promotion : listPromotion) {
            if (!promotion.getEndDate().toInstant().atZone(databaseTimeZone).toLocalDateTime().isAfter(currentDate)) {
                return false;
            }
        }
        //Check have only one promotion of discount and one of shipping
        int shipping = 0;
        int discount = 0;
        for (Promotion promotion : listPromotion) {
            if (promotion.getType().equals(PromotionType.SHIPPING)) {
                shipping++;
            } else if (promotion.getType().equals(PromotionType.DISCOUNT)) {
                discount++;
            }
        }
        if (shipping > 1 || discount > 1) {
            return false;
        }

        //Calculate total price of all product for check condition of promotion
        double totalPriceOfAllProduct = calculateTotalPriceOfAllProduct(productOrder);
        log.info("----------------------------------------------------------------------");
        log.info("checkPromotion() totalPriceOfAllProduct: {}", totalPriceOfAllProduct);
        //Check promotion can use with order
        boolean checkConditionPrice = listPromotion.stream()
                .allMatch(
                        promotion -> totalPriceOfAllProduct >= promotion.getMinimumOrderValue()
                );
        if (!checkConditionPrice) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkTotalShopPrice(List<ItemByShopDto> itemsByShop) {
        for (ItemByShopDto item : itemsByShop) {
            Map<Long, Integer> productQuantityMap = item.getListItems();
            if (item.getTotalShopPrice() != calculateTotalPriceOfAllProduct(productQuantityMap)) {
                log.info("------------------------------checkTotalShopPrice()--------------------------------");
                log.info("item.getTotalShopPrice() != calculateTotalPriceOfAllProduct(productQuantityMap)");
                log.info("item.getTotalShopPrice() {}", item.getTotalShopPrice());
                log.info("calculateTotalPriceOfAllProduct(productQuantityMap) {}", calculateTotalPriceOfAllProduct(productQuantityMap));
                log.info("--------------------------------------------------------------");
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean checkSubTotal(double subTotal, Map<Long, Integer> productOrder) {
        if (productOrder == null || productOrder.isEmpty()) {
            return false;
        }
        log.info("----------------------------checkSubTotal()----------------------------------");
        log.info("item.getTotalShopPrice() != calculateTotalPriceOfAllProduct(productQuantityMap)");
        log.info("subTotal {}", subTotal);
        log.info("calculateTotalPriceOfAllProduct(productOrder) {}", calculateTotalPriceOfAllProduct(productOrder));
        log.info("--------------------------------------------------------------");
        return subTotal == calculateTotalPriceOfAllProduct(productOrder);
    }

    @Override
    public boolean checkTotalShippingFee(PackageOrderRequestDto packageOrder) {
        //Check when have promotion with type SHIPPING
        if (packageOrder.getCartInfo().getPromotionIds() != null && !packageOrder.getCartInfo().getPromotionIds().isEmpty()) {
            List<Promotion> promotions = findPromotions(packageOrder.getCartInfo().getPromotionIds());
            for (Promotion promotion : promotions) {
                if (promotion.getType().equals(PromotionType.SHIPPING) && packageOrder.getCartInfo().getTotal().getShippingTotal() == 0) {
                    return true;
                }
            }
        }

        //Check shipping fee when don't have promotion
        final double[] totalShip = {0};
        //Check shipping fee each shop
        boolean checkShippingFeeEachOrder = packageOrder.getCartInfo().getItemsByShop().stream()
                .allMatch(item -> {
                    try {
                        double shippingFeeWithDistance = getShippingFeeByDistance(item.getDistance());
                        if (shippingFeeWithDistance == -1) {
                            return false;
                        }
                        totalShip[0] += shippingFeeWithDistance;
                        return item.getShippingFee() == shippingFeeWithDistance;
                    } catch (JsonProcessingException e) {
                        return false;
                    }
                });
        log.info("----------------------------checkTotalShippingFee()----------------------------------");
        log.info("checkShippingFeeEachOrder {}", checkShippingFeeEachOrder);
        log.info("Math.round(totalShip[0] * 100.0) / 100.0) {}", Math.round(totalShip[0] * 100.0) / 100.0);
        log.info("packageOrder.getCartInfo().getTotal().getShippingTotal() {}", packageOrder.getCartInfo().getTotal().getShippingTotal());
        log.info("--------------------------------------------------------------");
        return checkShippingFeeEachOrder && (Math.round(totalShip[0] * 100.0) / 100.0) == packageOrder.getCartInfo().getTotal().getShippingTotal();
    }

    @Override
    public boolean checkTotalDiscount(PackageOrderRequestDto packageOrder) {
        //Check when have promotion with type DISCOUNT
        if (packageOrder.getCartInfo().getPromotionIds() != null && !packageOrder.getCartInfo().getPromotionIds().isEmpty()) {
            List<Promotion> promotions = findPromotions(packageOrder.getCartInfo().getPromotionIds());
            for (Promotion promotion : promotions) {
                if (
                        promotion.getType().equals(PromotionType.DISCOUNT)
                                && packageOrder.getCartInfo().getTotal().getPromotionFee() == promotion.getDiscount()
                ) {
                    log.info("packageOrder.getCartInfo().getTotal().getPromotionFee() {}", packageOrder.getCartInfo().getTotal().getPromotionFee());
                    log.info("promotion.getDiscount()", promotion.getDiscount());
                    return true;
                }
            }
        }
        if (packageOrder.getCartInfo().getTotal().getPromotionFee() == 0) {
            log.info("Discount 0");
            return true;
        } else {
            log.info("Discount false");
            return false;
        }
    }

    @Override
    public boolean checkTotalPayment(TotalOrderDto totalOrderDto) {
        double totalPayment = totalOrderDto.getSubTotal() + totalOrderDto.getShippingTotal() - totalOrderDto.getPromotionFee();
        log.info("----------------------------checkTotalPayment()--------------------------------------------");
        log.info("totalOrderDto.getPaymentTotal() {}", totalOrderDto.getPaymentTotal());
        log.info("Math.round(totalPayment * 100.0) / 100.0 {}", Math.round(totalPayment * 100.0) / 100.0);
        log.info("------------------------------------------------------------------------");
        return totalOrderDto.getPaymentTotal() == Math.round(totalPayment * 100.0) / 100.0;
    }

    private double calculatePriceAfterAddVoucher(double totalPrice, List<Promotion> promotions) {
        return Math.round(
                (
                        promotions.stream()
                                .mapToDouble(
                                        promotion -> {
                                            if (promotion.getType().equals(PromotionType.DISCOUNT)) {
                                                return promotion.getDiscount();
                                            } else if (promotion.getType().equals(PromotionType.SHIPPING)) {
                                                return 0;
                                            }
                                            return 0;
                                        })
                                .reduce(totalPrice, (subtotal, discount) -> subtotal - discount)
                ) * 100.0) / 100.0;
    }

    private double calculateTotalPriceOfAllProduct(Map<Long, Integer> productOrder) {
        return Math.round(
                (
                        productOrder.entrySet().stream().mapToDouble(
                                entry -> {
                                    Long productId = entry.getKey();
                                    Integer quantity = entry.getValue();
                                    Optional<Product> product = productRepository.findById(productId);
                                    double saleOff = productService.CalculateSaleOff(product.get().getPromotionShops(), product.get().getPrice());
                                    double priceAfterDiscounted = productService.CalculateDiscountedPrice(product.get().getPrice(), saleOff);
                                    return Math.round((priceAfterDiscounted * quantity) * 100.0) / 100.0;
                                }
                        ).sum()
                ) * 100.0
        ) / 100.0;
    }

    private PackageOrder savePackageOrder(
            PackageOrderRequestDto packageOrderRequest, Account account, Transaction transaction
    ) {
        Address address = new Address();
        address.setPhone(packageOrderRequest.getUserInfo().getPhoneNumber());
        address.setFullName(packageOrderRequest.getUserInfo().getFullName());
        address.setAddress(packageOrderRequest.getUserInfo().getAddress());
        Address saveShippingAddress = addressRepository.save(address);
        PackageOrder packageOrder = PackageOrder.builder()
                .totalPrice(packageOrderRequest.getCartInfo().getTotal().getPaymentTotal())
                .discount(packageOrderRequest.getCartInfo().getTotal().getPromotionFee())
                .paymentMethod(packageOrderRequest.getCartInfo().getPaymentMethod())
                .account(account)
                .transaction(transaction)
                .shippingAddress(saveShippingAddress)
                .build();
        if (
                packageOrderRequest.getCartInfo().getPromotionIds() != null
                        && !packageOrderRequest.getCartInfo().getPromotionIds().isEmpty()
        ) {
            packageOrder.setPromotions(findPromotions(packageOrderRequest.getCartInfo().getPromotionIds()));
        }
        PackageOrder savePackageOrder = packageOrderRepository.save(packageOrder);
        return savePackageOrder;
    }

    private List<Order> saveOrder(PackageOrder packageOrder, PackageOrderRequestDto packageOrderRequestDto, Map<Long, Integer> productOrder) {
        List<Order> orderList = new ArrayList<>();
        List<Long> productListId = getListProductId(productOrder);

        List<Product> products = productRepository.findAllById(productListId);
        products.stream().forEach(s -> log.info("pro {}", s.getId()));

        List<ShopOwner> shops = getListShopOwners(products);
        shops.stream().forEach(s -> log.info("shop {}", s.getId()));

        List<ItemByShopDto> itemsByShop = packageOrderRequestDto.getCartInfo().getItemsByShop();
        shops.stream().forEach(shopOwner -> {
            ItemByShopDto itemByShop = itemsByShop.stream()
                    .filter(itemByShopDto -> itemByShopDto.getShopId().equals(shopOwner.getId()))
                    .findFirst().get();
//            List<PromotionShop> promotionShops = products.stream()
//                    .filter(
//                            product -> product.getShopOwner().equals(shopOwner)
//                    ).map(Product::getPromotionShops)
//                    .flatMap(List::stream)
//                    .collect(Collectors.toList());
            Order order = Order.builder()
                    .totalPrice(itemByShop.getTotalShopPrice())
                    .shippingFee(itemByShop.getShippingFee())
                    .status(OrderStatus.PENDING)
                    .shopOwner(shopOwner)
                    .packageOrder(packageOrder)
                    .build();
            Order saveOrder = orderRepository.save(order);
            orderList.add(saveOrder);
        });
        //push notification for shop
        List<Long> userIdOfShopList = shops.stream().map(shop -> shop.getAccount().getId()).toList();
        log.info("Here is total shop {}", userIdOfShopList.size());
        log.info("Here is account shopid {}", userIdOfShopList.toString());
        NotificationDto notificationDto = new NotificationDto();
        notificationDto.setRole(NotifiConstant.NOTI_SHOP_ROLE);
        notificationDto.setName(NotifiConstant.NEW_ORDER_FOR_SHOP_OWNER_NAME);
        notificationDto.setNotiText(NotifiConstant.NEW_ORDER_FOR_SHOP_OWNER_CONTENT);
        notificationService.pushNotificationForListUserID(userIdOfShopList, notificationDto);
        return orderList;
    }

    private List<OrderDetail> saveOrderDetails(List<Order> orders, Map<Long, Integer> productOrder) {
        List<Long> productListId = getListProductId(productOrder);
        List<Product> products = productRepository.findAllById(productListId);

        List<ShopOwner> shopOwners = getListShopOwners(products);
        List<OrderDetail> orderDetails = new ArrayList<>();
//        for (Order order : orders) {
//            for (ShopOwner shopOwner : shopOwners) {
//                if (order.getShopOwner().equals(shopOwner)) {
//                    for (Product product : products) {
//                        if (product.getShopOwner().equals(shopOwner)) {
//                            double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
//                            double discountedPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
//                            OrderDetail orderDetail = OrderDetail.builder()
//                                    .order(order)
//                                    .product(product)
//                                    .price(discountedPrice)
//                                    .quantity(packageOrderRequestDto.getProductOrder().get(product.getId()))
//                                    .build();
//                            orderDetails.add(orderDetail);
//                            orderDetailRepository.save(orderDetail);
//                        }
//                    }
//                }
//            }
//        }

        orders.stream()
                .forEach(
                        order -> products.stream()
                                .filter(product -> product.getShopOwner().equals(order.getShopOwner()))
                                .forEach(product -> {
                                    int newQuantity = product.getQuantity() - productOrder.get(product.getId());
                                    double saleOff = productService.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
                                    double discountedPrice = productService.CalculateDiscountedPrice(product.getPrice(), saleOff);
                                    ArrayList<PromotionShop> promotionShops = new ArrayList<>(product.getPromotionShops());
                                    OrderDetail orderDetail = OrderDetail.builder()
                                            .order(order)
                                            .product(product)
                                            .price(discountedPrice)
                                            .quantity(productOrder.get(product.getId()))
                                            .promotionShops(promotionShops)
                                            .productPromotionRate(promotionPriceService.calculatePercentDiscountedOfProductByPromotions(promotionShops, discountedPrice))
                                            .build();
                                    product.setQuantity(newQuantity);
                                    productRepository.save(product);
                                    OrderDetail saveOrderDetail = orderDetailRepository.save(orderDetail);
                                    orderDetails.add(saveOrderDetail);
                                })
                );
        return orderDetails;
    }

    private Long saveAll(
            PackageOrderRequestDto packageOrderRequestDto, String paymentId, String payerEmail,
            Account account, Map<Long, Integer> productOrder
    ) {
        Transaction transaction = Transaction.builder()
                .amount(packageOrderRequestDto.getCartInfo().getTotal().getPaymentTotal())
                .status(TransactionStatus.PROCESSING)
                .build();
        if (packageOrderRequestDto.getCartInfo().getPaymentMethod().equals(PaymentMethod.PAYPAL)) {
            transaction.setPaypalId(paymentId);
            transaction.setPaypalEmail(payerEmail);
            transaction.setStatus(TransactionStatus.SUCCESS);
        }
        Transaction saveTransaction = transactionRepository.save(transaction);
        PackageOrder packageOrder = savePackageOrder(packageOrderRequestDto, account, saveTransaction);
        List<Order> orders = saveOrder(packageOrder, packageOrderRequestDto, productOrder);
        saveOrderDetails(orders, productOrder);
        return packageOrder.getId();
    }

    private ResponseEntity<?> handleInitialPayment(PackageOrderRequestDto packageOrderRequestDto, Account account) {
        // Handle initial payment request
        try {
            String description = account.getEmail()
                    + " pay with paypal for " + packageOrderRequestDto.getCartInfo().getTotal().getPaymentTotal();
            PaymentDto paymentDto = PaymentDto.builder()
                    .total(packageOrderRequestDto.getCartInfo().getTotal().getPaymentTotal())
                    .currency(Currency.USD.toString())
                    .method(PaymentMethod.PAYPAL)
                    .intent(PaypalPaymentIntent.SALE)
                    .description(description)
                    .successUrl(appProperties.getPaypal().getSuccessUrl())
                    .cancelUrl(appProperties.getPaypal().getCancelUrl())
                    .build();
            Payment payment = paypalService.createPayment(paymentDto);
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    log.info("link approval_url {}", link.getHref());
                    SuccessResponse successResponse = SuccessResponse.builder()
                            .successCode(String.valueOf(HttpStatus.OK.value()))
                            .successMessage("Redirect: " + link.getHref())
                            .build();
                    return new ResponseEntity<>(successResponse, HttpStatus.OK);
                }
            }
        } catch (PayPalRESTException e) {
            e.printStackTrace();
        }
        log.info("fail");
        ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()),
                "Payment with paypal failed.");
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }

    private ResponseEntity<?> handleSuccessPayment(
            PackageOrderRequestDto packageOrderRequestDto,
            Map<Long, Integer> productOrder,
            Account account,
            String paymentId,
            String payerId
    ) {
        // Handle success payment
        try {
            Payment payment = paypalService.executePayment(paymentId, payerId);
            log.info("Payment {}", payment.toJSON());
            log.info("payerId id{}", payerId);
            log.info("paymentId id{}", paymentId);
            if (payment.getState().equals("approved")) {
                Long packageOrderId;
                if (transactionRepository.findByPaypalId(paymentId).isPresent()) {
                    ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()),
                            "paymentId " + paymentId + " already exist.");
                    return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
                } else {
                    String payerEmail = payment.getPayer().getPayerInfo().getEmail();
                    updateTotalOrderOfListProduct(productOrder.keySet().stream().toList());
                    packageOrderId = saveAll(packageOrderRequestDto, paymentId, payerEmail, account, productOrder);
                }
                SuccessResponse successResponse = SuccessResponse.builder()
                        .successCode(String.valueOf(HttpStatus.OK.value()))
                        .successMessage("Payment with paypal successful. packageOrderId=" + packageOrderId)
                        .build();

                return ResponseEntity.status(HttpStatus.OK)
                        .body(successResponse);
            }
        } catch (PayPalRESTException e) {
            ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()),
                    "Payment with paypal failed.");
            return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
        }
        ErrorResponse error = new ErrorResponse(String.valueOf(HttpStatus.EXPECTATION_FAILED.value()),
                "Payment with paypal failed.");
        return new ResponseEntity<>(error, HttpStatus.EXPECTATION_FAILED);
    }

    private Map<Long, Integer> getAllProductWithQuantity(List<ItemByShopDto> itemsByShop) {
        Map<Long, Integer> productQuantityMap = new HashMap<>();
        if (itemsByShop != null && !itemsByShop.isEmpty()) {
            for (ItemByShopDto item : itemsByShop) {
                item.getListItems().entrySet()
                        .forEach(
                                entry -> {
                                    productQuantityMap.put(entry.getKey(), entry.getValue());
                                });
            }
        }
        return productQuantityMap;
    }

    private List<Promotion> findPromotions(List<Long> idsPromotion) {
        return promotionRepository.findAllById(idsPromotion);
    }

    private List<ShopOwner> getListShopOwners(List<Product> products) {
        Comparator<ShopOwner> shopOwnerComparator = Comparator.comparing(ShopOwner::getId);
        return products.stream()
                .map(Product::getShopOwner)
                .distinct()
                .sorted(shopOwnerComparator)
                .collect(Collectors.toList());
    }

    private List<Long> getListProductId(Map<Long, Integer> productOrder) {
        return productOrder.entrySet()
                .stream()
                .map(
                        entry -> entry.getKey()
                )
                .collect(Collectors.toList());
    }

    private double getShippingFeeByDistance(double distance) throws JsonProcessingException {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = appProperties.getShip().getUrl();

        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl)
                .queryParam("distance", distance);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                String responseBody = response.getBody();

                // Parse the JSON response
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonMap = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
                });

                // Access the 'shippingFee' field
                Double shippingFee = (Double) jsonMap.get("shippingFee");
                return shippingFee;
            } else if (response.getStatusCode() == HttpStatus.NOT_ACCEPTABLE) {
                return -1;
            }
        } catch (Exception ex) {
            return -1;
        }
        return -1;
    }

    private boolean updateTotalOrderOfListProduct(List<Long> ids) {
        ids.forEach(id -> {
            Product product = new Bird();
            product.setId(id);
            productSummaryService.updateTotalQuantityOrder(product);
        });
        return true;
    }

    private PackageOrderAdminDto packageOrderToPackageOrderAdminDto(PackageOrder packageOrder) {
        List<Order> orders = packageOrder.getOrders();
        int countSuccess;
        if (packageOrder.getTransaction().getStatus().equals(TransactionStatus.SUCCESS)) {
            countSuccess = orders.size();
        } else {
            countSuccess = (int) orders.stream().filter(order -> order.getStatus().equals(OrderStatus.DELIVERED)).count();
        }
        return PackageOrderAdminDto.builder()
                .id(packageOrder.getId())
                .accountId(packageOrder.getAccount().getId())
                .paymentMethod(packageOrder.getPaymentMethod())
                .payerEmail(packageOrder.getTransaction().getPaypalEmail())
                .transactionStatus(packageOrder.getTransaction().getStatus())
                .totalPayment(packageOrder.getTotalPrice())
                .createdDateTransaction(packageOrder.getTransaction().getTransactionDate().getTime())
                .lastedUpdateTransaction(packageOrder.getTransaction().getLastedUpdate().getTime())
                .fullName(packageOrder.getShippingAddress().getFullName())
                .phoneNumber(packageOrder.getShippingAddress().getPhone())
                .address(packageOrder.getShippingAddress().getAddress())
                .statusOrders(countSuccess + "/" + orders.size())
                .build();
    }

    private PackageOrderDto packageOrderToPackageOrderDto(PackageOrder packageOrder) {
        List<Order> orders = packageOrder.getOrders();
        double totalPriceProduct = orders.stream().mapToDouble(Order::getTotalPrice).sum();
        double shippingFee = orders.stream().mapToDouble(Order::getShippingFee).sum();
        return PackageOrderDto.builder()
                .id(packageOrder.getId())
                .createdDate(packageOrder.getCreatedDate())
                .lastedUpdate(packageOrder.getLastedUpdate())
                .paymentMethod(packageOrder.getPaymentMethod())
                .address(packageOrder.getShippingAddress().getAddress())
                .totalPriceProduct(Math.round(totalPriceProduct * 100.0) / 100.0)
                .shippingFee(Math.round(shippingFee * 100.0) / 100.0)
                .discount(packageOrder.getDiscount())
                .totalPayment(packageOrder.getTotalPrice())
                .build();
    }
}
