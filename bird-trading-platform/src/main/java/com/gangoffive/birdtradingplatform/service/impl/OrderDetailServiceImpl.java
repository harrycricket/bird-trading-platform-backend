package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.OrderDetailShopOwnerDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.Review;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.service.OrderDetailService;
import com.gangoffive.birdtradingplatform.service.PromotionPriceService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {
    private final PromotionPriceService promotionPriceService;
    private final OrderDetailRepository orderDetailRepository;
    private final AccountRepository accountRepository;

    @Override
    public ResponseEntity<?> getAllOrderByShopOwner(int pageNumber) {
        Optional<Account> account = accountRepository.findByEmail(
                SecurityContextHolder.getContext().getAuthentication().getName()
        );
        PageRequest pageRequest = PageRequest.of(pageNumber, PagingAndSorting.DEFAULT_PAGE_SHOP_SIZE);
        Optional<Page<OrderDetail>> orderDetails = orderDetailRepository.findAllByOrder_ShopOwner(account.get().getShopOwner(), pageRequest);
        List<OrderDetailShopOwnerDto> orderDetailShopOwnerDto = orderDetails.get().stream().map(this::orderDetailToOrderDetailShopOwnerDto).toList();
        return ResponseEntity.ok(orderDetailShopOwnerDto);
    }


    private OrderDetailShopOwnerDto orderDetailToOrderDetailShopOwnerDto(OrderDetail orderDetail) {
        Review review = orderDetail.getReview();
        Product product = orderDetail.getProduct();
        double promotionRate = promotionPriceService.calculatePercentDiscountedOfProductByPromotions(
                orderDetail.getPromotionShops(), orderDetail.getPrice()
        );
        OrderDetailShopOwnerDto orderDetailShopOwnerDto = OrderDetailShopOwnerDto.builder()
                .orderId(orderDetail.getOrder().getId())
                .orderDetailId(orderDetail.getId())
                .createDate(orderDetail.getOrder().getCreatedDate().getTime())
                .productId(product.getId())
                .nane(product.getName())
                .price(orderDetail.getPrice())
                .quantity(orderDetail.getQuantity())
                .promotionRate(promotionRate)
                .build();
        if (review != null) {
            orderDetailShopOwnerDto.setReviewRating(review.getRating().getStar());
        }
        return orderDetailShopOwnerDto;
    }
}
