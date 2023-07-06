package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.ReviewDto;
import com.gangoffive.birdtradingplatform.entity.Review;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ResponseEntity<?> getAllReviewByOrderId(Long orderId);
    ResponseEntity<?> addNewReviewByOrderDetailId(List<MultipartFile> multipartFiles, ReviewDto review);
    ReviewDto reviewToReviewDto(Review review);
}
