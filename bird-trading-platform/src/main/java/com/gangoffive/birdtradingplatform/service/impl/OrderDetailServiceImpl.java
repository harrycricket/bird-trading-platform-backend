package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.DateRangeDto;
import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerDto;
import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerFilterDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.Review;
import com.gangoffive.birdtradingplatform.enums.FieldOrderDetailTable;
import com.gangoffive.birdtradingplatform.enums.Operator;
import com.gangoffive.birdtradingplatform.enums.SortOrderDetailColumn;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.service.OrderDetailService;
import com.gangoffive.birdtradingplatform.util.DateUtils;
import com.gangoffive.birdtradingplatform.util.JsonUtil;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {
    private final OrderDetailRepository orderDetailRepository;
    private final AccountRepository accountRepository;

    @Override
    public ResponseEntity<?> getAllOrderByShopOwner(OrderDetailShopOwnerFilterDto orderDetailFilter) {
        Optional<Account> account = accountRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        Long shopId = account.get().getShopOwner().getId();
        if (orderDetailFilter.getPageNumber() > 0) {
            int pageNumber = orderDetailFilter.getPageNumber() - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
            PageRequest pageRequestWithSort = null;

            if (orderDetailFilter.getSortDirection() != null
                    && !orderDetailFilter.getSortDirection().getSort().isEmpty()
                    && !orderDetailFilter.getSortDirection().getField().isEmpty()
            ) {
                if (
                        !SortOrderDetailColumn.checkField(orderDetailFilter.getSortDirection().getField())
                ) {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                            .errorMessage("Not found this field in sort direction.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
                if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
                    pageRequestWithSort = getPageRequest(orderDetailFilter, pageNumber, Sort.Direction.ASC);
                } else if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
                    pageRequestWithSort = getPageRequest(orderDetailFilter, pageNumber, Sort.Direction.DESC);
                } else {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                            .errorMessage("Not found this direction.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
            }

            if (
                    orderDetailFilter.getOrderSearchInfo().getField().isEmpty()
                            && orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getOrderSearchInfo().getOperator().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                log.info("all no");
                return filterAllOrderDetailAllFieldEmpty(shopId, pageRequest);
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().isEmpty()
                            && orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getOrderSearchInfo().getOperator().isEmpty()
                            && !orderDetailFilter.getSortDirection().getField().isEmpty()
                            && !orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                log.info("with sort");
                if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
//                    Sort.Direction sortDirection;
//                    if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
//                        sortDirection = Sort.Direction.DESC;
//                    } else {
//                        sortDirection = Sort.Direction.ASC;
//                    }
                    return filterAllOrderDetailAllFieldEmptySortWithRating(
                            shopId, orderDetailFilter.getSortDirection().getSort(), pageRequest
                    );
                } else {
                    return filterAllOrderDetailAllFieldEmpty(shopId, pageRequestWithSort);
                }
            }
            if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.ID.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterOrderDetailByOrderDetailIdEqual(orderDetailFilter, shopId, pageRequest);
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.ORDER_ID.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterOrderDetailByOrderIdEqual(orderDetailFilter, shopId, pageRequest);
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.ORDER_ID.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                        return filterOrderDetailByOrderIdEqualSortWithRating(orderDetailFilter, shopId, pageRequest);
                    } else {
                        return filterOrderDetailByOrderIdEqual(orderDetailFilter, shopId, pageRequestWithSort);
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PRODUCT_ID.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    return filterOrderDetailByProductIdEqual(orderDetailFilter, shopId, pageRequest);
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PRODUCT_ID.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.EQUAL.getOperator())) {
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                        return filterOrderDetailByProductIdEqualSortWithRating(orderDetailFilter, shopId, pageRequest);
                    } else {
                        return filterOrderDetailByProductIdEqual(orderDetailFilter, shopId, pageRequestWithSort);
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PRODUCT_NAME.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    return filterOrderDetailByProductNameContain(orderDetailFilter, shopId, pageRequest);
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PRODUCT_NAME.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.CONTAIN.getOperator())) {
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                        List<OrderDetail> orderDetails = orderDetailRepository.findAllByProduct_NameLikeAndOrderShopOwner_Id(
                                        "%" + orderDetailFilter.getOrderSearchInfo().getValue() + "%", shopId
                                );

                        return pagingWithOrderDetailsIdSortWithRating(
                                orderDetailFilter, orderDetails.stream().map(OrderDetail::getId).toList(),
                                shopId, pageRequest
                        );
                    } else {
                        return filterOrderDetailByProductNameContain(orderDetailFilter, shopId, pageRequestWithSort);
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PRICE.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterOrderDetailByPriceGreaterThanOrEqual(orderDetailFilter, shopId, pageRequest);
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PRICE.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                        return filterOrderDetailByPriceGreaterThanOrEqualSortWithRating(orderDetailFilter, shopId, pageRequest);
                    } else {
                        return filterOrderDetailByPriceGreaterThanOrEqual(orderDetailFilter, shopId, pageRequestWithSort);
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PROMOTION_RATE.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterOrderDetailByPromotionRateGreaterThanOrEqual(orderDetailFilter, shopId, pageRequest);
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.PROMOTION_RATE.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                        return filterOrderDetailByPromotionRateGreaterThanOrEqualSortWithRating(orderDetailFilter, shopId, pageRequest);
                    } else {
                        return filterOrderDetailByPromotionRateGreaterThanOrEqual(orderDetailFilter, shopId, pageRequestWithSort);
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.REVIEW_RATING.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    return filterOrderDetailByReviewRatingGreaterThanEqual(orderDetailFilter, shopId, pageRequest);
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.REVIEW_RATING.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.GREATER_THAN_OR_EQUAL.getOperator())) {
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                        List<OrderDetail> orderDetails = orderDetailRepository.findAllByReview_RatingStarGreaterThanEqualAndOrder_ShopOwner_Id(
                                Integer.parseInt(orderDetailFilter.getOrderSearchInfo().getValue()), shopId
                        );

                        return pagingWithOrderDetailsIdSortWithRating(
                                orderDetailFilter, orderDetails.stream().map(OrderDetail::getId).toList(),
                                shopId, pageRequest
                        );
                    } else {
                        return filterOrderDetailByReviewRatingGreaterThanEqual(orderDetailFilter, shopId, pageRequestWithSort);
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.CREATED_DATE.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
                            && orderDetailFilter.getSortDirection().getField().isEmpty()
                            && orderDetailFilter.getSortDirection().getSort().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(orderDetailFilter.getOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        return filterOrderByCreatedDateGreaterThanOrEqual(shopId, dateRange, pageRequest);
                    } else {
                        return filterOrderDetailByCreatedDateFromTo(shopId, dateRange, pageRequest);
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else if (
                    orderDetailFilter.getOrderSearchInfo().getField().equals(FieldOrderDetailTable.CREATED_DATE.getField())
                            && !orderDetailFilter.getOrderSearchInfo().getValue().isEmpty()
            ) {
                if (orderDetailFilter.getOrderSearchInfo().getOperator().equals(Operator.FROM_TO.getOperator())) {
                    DateRangeDto dateRange = JsonUtil.INSTANCE.getObject(orderDetailFilter.getOrderSearchInfo().getValue(), DateRangeDto.class);
                    if (dateRange.getDateTo() == -1L) {
                        if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                            return filterOrderByCreatedDateGreaterThanOrEqualSortWithRating(orderDetailFilter, shopId, dateRange, pageRequest);
                        } else {
                            return filterOrderByCreatedDateGreaterThanOrEqual(shopId, dateRange, pageRequestWithSort);
                        }
                    } else {
                        if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.REVIEW_RATING.getField())) {
                            return filterOrderDetailByCreatedDateFromToSortWithRating(orderDetailFilter, shopId, dateRange, pageRequest);
                        } else {
                            return filterOrderDetailByCreatedDateFromTo(shopId, dateRange, pageRequestWithSort);
                        }
                    }
                }
                return getErrorResponseNotFoundOperator();
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .errorMessage("Order detail filter is not correct.")
                        .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        } else {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                    "Page number cannot less than 1");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    private ResponseEntity<?> filterOrderDetailByCreatedDateFromTo(
            Long shopId, DateRangeDto dateRange, PageRequest pageRequest
    ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByOrder_CreatedDateBetweenAndOrder_ShopOwner_Id(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                calendar.getTime(),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found order detail have created date from to.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderByCreatedDateGreaterThanOrEqual(
            Long shopId, DateRangeDto dateRange, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByOrder_CreatedDateGreaterThanEqualAndOrder_ShopOwner_Id(
                DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found order detail have created date greater than or equal.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    private ResponseEntity<?> filterOrderDetailByReviewRatingGreaterThanEqual(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByReview_RatingStarGreaterThanEqualAndOrder_ShopOwner_Id(
                Integer.parseInt(orderDetailFilter.getOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found review rating from to.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByPromotionRateGreaterThanOrEqual(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByProductPromotionRateGreaterThanEqualAndOrder_ShopOwner_Id(
                Double.parseDouble(orderDetailFilter.getOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found promotion rate have greater than or equal this value.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByPriceGreaterThanOrEqual(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByPriceGreaterThanEqualAndOrder_ShopOwner_Id(
                Double.parseDouble(orderDetailFilter.getOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found product have greater than or equal this price.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByProductNameContain(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByProduct_NameLikeAndOrder_ShopOwner_Id(
                "%" + orderDetailFilter.getOrderSearchInfo().getValue() + "%",
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found contain this product name.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByProductIdEqual(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByProduct_IdAndOrder_ShopOwner_Id(
                Long.valueOf(orderDetailFilter.getOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this product id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByOrderIdEqual(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByOrder_IdAndOrder_ShopOwner_Id(
                Long.valueOf(orderDetailFilter.getOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this order id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByOrderDetailIdEqual(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findByIdAndOrder_ShopOwner_Id(
                Long.valueOf(orderDetailFilter.getOrderSearchInfo().getValue()),
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this order detail id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> getErrorResponseNotFoundOperator() {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this operator.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByCreatedDateFromToSortWithRating(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId,
            DateRangeDto dateRange, PageRequest pageRequest
    ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.timeInMillisecondToDate(dateRange.getDateTo()));
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        Optional<Page<OrderDetail>> orderDetails;
        if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByOrderCreatedDateBetweenAndOrderShopOwnerIdSortByReviewRatingASC(
                    DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                    calendar.getTime(),
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByOrderCreatedDateBetweenAndOrderShopOwnerIdSortByReviewRatingDESC(
                    DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                    calendar.getTime(),
                    shopId,
                    pageRequest
            );
        }

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found order detail have created date from to.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderByCreatedDateGreaterThanOrEqualSortWithRating(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, DateRangeDto dateRange, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails;
        if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByOrderCreatedDateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
                    DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByOrderCreatedDateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
                    DateUtils.timeInMillisecondToDate(dateRange.getDateFrom()),
                    shopId,
                    pageRequest
            );
        }

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found order detail have created date greater than or equal.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    private ResponseEntity<?> filterOrderDetailByPromotionRateGreaterThanOrEqualSortWithRating(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails;
        if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByProductPromotionRateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
                    Double.parseDouble(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByProductPromotionRateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
                    Double.parseDouble(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        }

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found promotion rate have greater than or equal this value.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByPriceGreaterThanOrEqualSortWithRating(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails;
        if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByPriceGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
                    Double.parseDouble(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByPriceGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
                    Double.parseDouble(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        }

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found product have greater than or equal this price.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> pagingWithOrderDetailsIdSortWithRating(
            OrderDetailShopOwnerFilterDto orderDetailFilter, List<Long> orderDetailsId, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails;
        if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByOrderDetailIdInAndOrderShopOwnerIdSortByReviewRatingASC(
                    orderDetailsId,
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByOrderDetailIdInAndOrderShopOwnerIdSortByReviewRatingDESC(
                    orderDetailsId,
                    shopId,
                    pageRequest
            );
        }

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found order detail.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByProductIdEqualSortWithRating(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails;
        if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByProductIdAndOrderShopOwnerIdSortByReviewRatingASC(
                    Long.valueOf(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByProductIdAndOrderShopOwnerIdSortByReviewRatingDESC(
                    Long.valueOf(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        }

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this product id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterOrderDetailByOrderIdEqualSortWithRating(
            OrderDetailShopOwnerFilterDto orderDetailFilter, Long shopId, PageRequest pageRequest
    ) {
        Optional<Page<OrderDetail>> orderDetails;
        if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByOrderIdAndShopOwnerIdAndSortByReviewRatingASC(
                    Long.valueOf(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByOrderIdAndShopOwnerIdAndSortByReviewRatingDESC(
                    Long.valueOf(orderDetailFilter.getOrderSearchInfo().getValue()),
                    shopId,
                    pageRequest
            );
        }
        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found this order id.")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<?> filterAllOrderDetailAllFieldEmptySortWithRating(Long shopId, String sortDirection, PageRequest pageRequest) {
        Optional<Page<OrderDetail>> orderDetails;
        if (sortDirection.toUpperCase().equals(Sort.Direction.ASC.name())) {
            orderDetails = orderDetailRepository.findAllByOrderShopOwnerIdAndSortByReviewRatingASC(
                    shopId,
                    pageRequest
            );
        } else {
            orderDetails = orderDetailRepository.findAllByOrderShopOwnerIdAndSortByReviewRatingDESC(
                    shopId,
                    pageRequest
            );
        }
        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                    .errorMessage("Not found order detail in shop.")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<?> filterAllOrderDetailAllFieldEmpty(Long shopId, PageRequest pageRequest) {
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByOrder_ShopOwner_Id(
                shopId,
                pageRequest
        );

        if (orderDetails.isPresent()) {
            return getPageNumberWrapperWithOrderDetails(orderDetails);
        } else {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                    .errorMessage("Not found order detail in shop.")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    private ResponseEntity<PageNumberWrapper<?>> getPageNumberWrapperWithOrderDetails(Optional<Page<OrderDetail>> orderDetails) {
        List<OrderDetailShopOwnerDto> orderDetailsShopOwnerDto = orderDetails.get().stream()
                .map(this::orderDetailToOrderDetailShopOwnerDto)
                .collect(Collectors.toList());
        PageNumberWrapper<OrderDetailShopOwnerDto> result = new PageNumberWrapper<>(
                orderDetailsShopOwnerDto,
                orderDetails.get().getTotalPages(),
                orderDetails.get().getTotalElements()
        );
        return ResponseEntity.ok(result);
    }

    private PageRequest getPageRequest(OrderDetailShopOwnerFilterDto orderDetailFilter, int pageNumber, Sort.Direction sortDirection) {
        return PageRequest.of(
                pageNumber,
                PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE,
                Sort.by(sortDirection,
                        SortOrderDetailColumn.getColumnByField(orderDetailFilter.getSortDirection().getField())
                )
        );
    }


    private OrderDetailShopOwnerDto orderDetailToOrderDetailShopOwnerDto(OrderDetail orderDetail) {
        Review review = orderDetail.getReview();
        Product product = orderDetail.getProduct();
        OrderDetailShopOwnerDto orderDetailShopOwnerDto = OrderDetailShopOwnerDto.builder()
                .orderId(orderDetail.getOrder().getId())
                .id(orderDetail.getId())
                .createdDate(orderDetail.getOrder().getCreatedDate().getTime())
                .productId(product.getId())
                .name(product.getName())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .promotionRate(orderDetail.getProductPromotionRate())
                .build();
        if (review != null) {
            orderDetailShopOwnerDto.setReviewRating(review.getRating().getStar());
        }
        return orderDetailShopOwnerDto;
    }
}
