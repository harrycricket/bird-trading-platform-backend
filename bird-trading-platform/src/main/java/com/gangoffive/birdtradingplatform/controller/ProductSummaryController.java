package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.entity.ProductSummary;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProductSummaryController {
    private final ProductSummaryService productSummaryService;

    @GetMapping("/test/calcu")
    public void testCalculate(){
        productSummaryService.updateReviewTotal(new Bird());
    }
}
