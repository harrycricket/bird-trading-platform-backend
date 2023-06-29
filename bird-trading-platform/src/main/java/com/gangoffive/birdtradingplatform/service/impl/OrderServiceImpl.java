package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.OrderStatusConstant;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import com.gangoffive.birdtradingplatform.enums.SortOrderColumn;
import com.gangoffive.birdtradingplatform.enums.SortProductColumn;
import com.gangoffive.birdtradingplatform.mapper.PromotionShopMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.service.OrderService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final PromotionShopMapper promotionShopMapper;

    @Override
    public ResponseEntity<?> getAllOrderByPackageOrderId(Long packageOrderId) {
        List<Order> orders = orderRepository.findAllByPackageOrder_Id(packageOrderId);

        return null;
    }

    @Override
    public ResponseEntity<?> getAllOrderByShopOwner(OrderShopOwnerFilterDto orderFilter) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<Account> account = accountRepository.findByEmail(email);
        Long shopId = account.get().getShopOwner().getId();
        if (orderFilter.getPageNumber() > 0) {
            int pageNumber = orderFilter.getPageNumber() - 1;
            PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
            PageRequest pageRequestWithSort = null;

            if (orderFilter.getSortDirection() != null
                    && !orderFilter.getSortDirection().getSort().isEmpty()
                    && !orderFilter.getSortDirection().getField().isEmpty()
            ) {
                if (!SortOrderColumn.checkField(orderFilter.getSortDirection().getField())) {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                            .errorMessage("Not found this field in sort direction.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
                }
                if (orderFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.ASC.name())) {
//                    pageRequestWithSort = getPageRequest(productFilter, pageNumber, Sort.Direction.ASC);
                } else if (orderFilter.getSortDirection().getSort().toUpperCase().equals(Sort.Direction.DESC.name())) {
//                    pageRequestWithSort = getPageRequest(productFilter, pageNumber, Sort.Direction.DESC);
                }
            }

            Optional<Page<Order>> orders = orderRepository.findByShopOwner_Id(shopId, pageRequest);
            if (orders.isPresent()) {
                List<OrderShopOwnerDto> orderShopOwnerDto = orders.get().stream().map(this::orderToOrderShopOwnerDto).collect(Collectors.toList());
                PageNumberWraper result = new PageNumberWraper<>(
                        orderShopOwnerDto,
                        orders.get().getTotalPages(),
                        orders.get().getTotalElements()
                );
                return ResponseEntity.ok(result);
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                        .errorMessage("Not found order in shop.")
                        .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
            }
        } else {
            ErrorResponse error = new ErrorResponse(HttpStatus.BAD_REQUEST.toString(),
                    "Page number cannot less than 1");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseEntity<?> updateStatusOfListOrder(ChangeStatusListIdDto changeStatusListIdDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Account> account = accountRepository.findByEmail(email);
        if (changeStatusListIdDto != null) {
            OrderStatus status = OrderStatus.getOrderStatusBaseOnStatusCode(changeStatusListIdDto.getStatus());
            if (OrderStatusConstant.UPDATE_ORDER_STATUS_SHOP_OWNER.contains(status)) {
                if (
                        orderRepository.checkIfOrderIdsBelongToShopId(
                                changeStatusListIdDto.getIds(),
                                account.get().getShopOwner().getId(),
                                changeStatusListIdDto.getIds().size()
                        )
                ) {
                    int result = orderRepository.updateStatusOfListId(status, changeStatusListIdDto.getIds());
                    if (result == changeStatusListIdDto.getIds().size()) {
                        return ResponseEntity.ok("Update success");
                    } else {
                        int numberUpdateFail = changeStatusListIdDto.getIds().size() - result;
                        return new ResponseEntity<>(ErrorResponse.builder().errorMessage(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                                .errorMessage(String.format("Update fail %d orders", numberUpdateFail)).build(), HttpStatus.BAD_REQUEST);
                    }
                } else {
                    ErrorResponse errorResponse = ErrorResponse.builder()
                            .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                            .errorMessage("Have order not of your shop.")
                            .build();
                    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
                }
            } else {
                ErrorResponse errorResponse = ErrorResponse.builder()
                        .errorCode(String.valueOf(HttpStatus.BAD_REQUEST.value()))
                        .errorMessage("This status can not change for shop owner")
                        .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
            }
        }
        return null;
    }

    private OrderDto orderToOrderDto(Order order) {
        return null;
    }

    private OrderShopOwnerDto orderToOrderShopOwnerDto(Order order) {
        OrderStatusDto orderStatus = OrderStatusDto.builder()
                .id(order.getStatus().getStatusCode())
                .status(order.getStatus())
                .build();
        return OrderShopOwnerDto.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .orderStatus(orderStatus)
                .shippingFee(order.getShippingFee())
                .paymentMethod(order.getPackageOrder().getPaymentMethod())
                .promotionsShop(order.getPromotionShops().stream().map(promotionShopMapper::modelToDto).collect(Collectors.toList()))
                .createdDate(order.getCreatedDate().getTime())
                .lastedUpdate(order.getLastedUpdate().getTime())
                .build();
    }
}
