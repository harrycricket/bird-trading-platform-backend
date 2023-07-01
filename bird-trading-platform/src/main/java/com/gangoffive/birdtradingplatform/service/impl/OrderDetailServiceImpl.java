package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerDto;
import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerFilterDto;
import com.gangoffive.birdtradingplatform.dto.OrderShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.Review;
import com.gangoffive.birdtradingplatform.enums.SortOrderDetailColumn;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.service.OrderDetailService;
import com.gangoffive.birdtradingplatform.service.PromotionPriceService;
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
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.PROMOTION_RATE.getField())) {
                        pageRequestWithSort = pageRequest;
                    } else {
                        pageRequestWithSort = getPageRequest(orderDetailFilter, pageNumber, Sort.Direction.ASC);
                    }
                } else if (orderDetailFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
                    if (orderDetailFilter.getSortDirection().getField().equals(SortOrderDetailColumn.PROMOTION_RATE.getField())) {
                        pageRequestWithSort = pageRequest;
                    } else {
                        pageRequestWithSort = getPageRequest(orderDetailFilter, pageNumber, Sort.Direction.DESC);
                    }
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
                return filterAllOrderDetailAllFieldEmpty(shopId, pageRequestWithSort);
            }


            Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByOrder_ShopOwner_Id(shopId, pageRequest);
            List<OrderDetailShopOwnerDto> orderDetailShopOwnerDto = orderDetails.get().stream().map(this::orderDetailToOrderDetailShopOwnerDto).toList();
            return ResponseEntity.ok(orderDetailShopOwnerDto);
        } else {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                    "Page number cannot less than 1");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
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
                .createDate(orderDetail.getOrder().getCreatedDate().getTime())
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
