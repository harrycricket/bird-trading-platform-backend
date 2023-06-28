package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.OrderDto;
import com.gangoffive.birdtradingplatform.dto.OrderShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.mapper.PromotionShopMapper;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderRepository;
import com.gangoffive.birdtradingplatform.service.OrderService;
import com.gangoffive.birdtradingplatform.wrapper.PageNumberWraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
    public ResponseEntity<?> getAllOrderByShopOwner(int pageNumber) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Optional<Account> account = accountRepository.findByEmail(email);
        PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_PRODUCT_SIZE);
        Optional<Page<Order>> orders = orderRepository.findByShopOwner(account.get().getShopOwner(), pageRequest);
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
    }

    private OrderDto orderToOrderDto(Order order) {
        return null;
    }

    private OrderShopOwnerDto orderToOrderShopOwnerDto(Order order) {
        return OrderShopOwnerDto.builder()
                .id(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .shippingFee(order.getShippingFee())
                .paymentMethod(order.getPackageOrder().getPaymentMethod())
                .promotionsShop(order.getPromotionShops().stream().map(promotionShopMapper::modelToDto).collect(Collectors.toList()))
                .createdDate(order.getCreatedDate().getTime())
                .lastedUpdate(order.getLastedUpdate().getTime())
                .build();
    }
}
