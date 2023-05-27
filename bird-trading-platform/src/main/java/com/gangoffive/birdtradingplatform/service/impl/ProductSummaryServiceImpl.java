package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.ProductSummary;
import com.gangoffive.birdtradingplatform.repository.OrderDetailRepository;
import com.gangoffive.birdtradingplatform.repository.ProductRepository;
import com.gangoffive.birdtradingplatform.repository.ProductSummaryRepository;
import com.gangoffive.birdtradingplatform.repository.ReviewRepository;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSummaryServiceImpl implements ProductSummaryService {
    private final ProductSummaryRepository productSummaryRepository;
    private final ProductService productService;
    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    public double updateProductStar(Product product) {
        double star = productService.CalculationRating(product.getOrderDetails());
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setStar(star);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return star;
    }

    public int updateReviewTotal(Product product){
        List<Long> orderDetailIds = product
                                        .getOrderDetails()
                                                .stream().
                                                    map(reviewId -> reviewId.getId()).collect(Collectors.toList());
        int reviewTotal = reviewRepository.findAllByOrderDetailIdIn(orderDetailIds).get().size();
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setReviewTotal(reviewTotal);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return reviewTotal;
    }

    public int updateTotalQuantityOrder(Product product){
        int totalQuantity = orderDetailRepository.findTotalQuantityByPId(product.getId()).orElse(0);
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setTotalQuantityOrder(totalQuantity);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return totalQuantity;
    }


    @Transactional
    public boolean apply(Product product){
        this.updateReviewTotal(product);
        this.updateProductStar(product);
        this.updateTotalQuantityOrder(product);
        return true;
    }

}
