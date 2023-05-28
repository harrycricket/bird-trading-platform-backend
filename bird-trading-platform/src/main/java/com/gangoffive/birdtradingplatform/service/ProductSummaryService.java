package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


public interface ProductSummaryService {
    double updateProductStar(Product product);
    int updateReviewTotal(Product product);
    int updateTotalQuantityOrder(Product product);

    boolean apply(Product product);
}
