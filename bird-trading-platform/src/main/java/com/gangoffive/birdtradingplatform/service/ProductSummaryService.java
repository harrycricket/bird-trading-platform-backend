package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.ProductFilterDto;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ProductSummaryService {
    double updateProductStar(Product product);
    int updateReviewTotal(Product product);
    int updateTotalQuantityOrder(Product product);
    String updateCategory(Product product);
    boolean apply(Product product);
    List<Long> getIdTopBird();

    List<Long> getIdTopAccessories();

    List<Long> getIdTopFood();

    double CalculationRating(List<OrderDetail> orderDetails);
}
