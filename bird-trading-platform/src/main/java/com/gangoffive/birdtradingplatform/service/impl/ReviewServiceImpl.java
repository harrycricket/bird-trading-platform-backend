package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.ReviewDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Review;
import com.gangoffive.birdtradingplatform.repository.AccountRepository;
import com.gangoffive.birdtradingplatform.repository.ReviewRepository;
import com.gangoffive.birdtradingplatform.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final AccountRepository accountRepository;
    @Override
    public ResponseEntity<?> getAllReviewByOrderId(Long orderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<Account> account = accountRepository.findByEmail(authentication.getName());
        Optional<List<Review>> reviews = reviewRepository.findAllByAccountAndOrderDetail_Order_Id(account.get(), orderId);
        if (reviews.isPresent() && reviews.get().size() > 0) {
            return ResponseEntity.ok(reviews.get().stream().map(this::reviewToReviewDto).collect(Collectors.toList()));
        }
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(String.valueOf(HttpStatus.NOT_FOUND.value()))
                .errorMessage("Not found reviews of this order id")
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @Override
    public ReviewDto reviewToReviewDto(Review review) {
        if (!review.getImgUrl().isEmpty()) {
            ReviewDto.builder()
                    .id(review.getId())
                    .orderDetailId(review.getOrderDetail().getId())
                    .productId(review.getOrderDetail().getProduct().getId())
                    .description(review.getComment())
                    .rating(review.getRating().getStar())
                    .imgUrl(Arrays.asList(review.getImgUrl().split(",")))
                    .reviewDate(review.getReviewDate().getTime())
                    .build();
        }
        return ReviewDto.builder()
                .id(review.getId())
                .orderDetailId(review.getOrderDetail().getId())
                .productId(review.getOrderDetail().getProduct().getId())
                .description(review.getComment())
                .rating(review.getRating().getStar())
                .reviewDate(review.getReviewDate().getTime())
                .build();
    }
}
