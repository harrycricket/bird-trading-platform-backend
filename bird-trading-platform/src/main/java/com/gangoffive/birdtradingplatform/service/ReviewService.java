package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.ReviewDto;
import com.gangoffive.birdtradingplatform.entity.Review;
import org.springframework.http.ResponseEntity;

public interface ReviewService {
    ResponseEntity<?> getAllReviewByOrderId(Long orderId);
    ReviewDto reviewToReviewDto(Review review);
}
