package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.ProductUpdateDto;
import com.gangoffive.birdtradingplatform.dto.ReviewDto;
import com.gangoffive.birdtradingplatform.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/users/reviews/products/{productId}")
    public ResponseEntity<?> getAllReviewByProductId(@PathVariable Long productId, @RequestParam int pageNumber) {
        return reviewService.getAllReviewByProductId(productId, pageNumber);
    }

    @GetMapping("/users/orders/{orderId}/reviews")
    public ResponseEntity<?> getAllReviewByOrderId(@PathVariable Long orderId) {
        return reviewService.getAllReviewByOrderId(orderId);
    }

    @PostMapping("/users/reviews")
    public ResponseEntity<?> updateProduct(
            @RequestParam(value = "image", required = false) List<MultipartFile> multipartFiles,
            @RequestPart(name = "data") ReviewDto review) {
        return reviewService.addNewReviewByOrderDetailId(multipartFiles, review);
    }
}
