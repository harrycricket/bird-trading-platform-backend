package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {
    private final ReviewService reviewService;
    @GetMapping("/users/orders/{orderId}/reviews")
    public ResponseEntity<?> getAllReviewByOrderId(@PathVariable Long orderId) {
        return reviewService.getAllReviewByOrderId(orderId);
    }

}
