package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.dto.ProductFilterDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.ProductService;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSummaryServiceImpl implements ProductSummaryService {
    private final ProductSummaryRepository productSummaryRepository;
    private final ReviewRepository reviewRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final BirdRepository birdRepository;
    private final FoodRepository foodRepository;
    private final AccessoryRepository accessoryRepository;

    public double updateProductStar(Product product) {
        double star = this.CalculationRating(product.getOrderDetails());
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setStar(star);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return star;
    }

    public int updateReviewTotal(Product product) {
        List<Long> orderDetailIds = product
                .getOrderDetails()
                .stream()
                .map(reviewId -> reviewId.getId()).collect(Collectors.toList());
        int reviewTotal = reviewRepository.findAllByOrderDetailIdIn(orderDetailIds).get().size();
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setReviewTotal(reviewTotal);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return reviewTotal;
    }

    public int updateTotalQuantityOrder(Product product) {
        int totalQuantity = orderDetailRepository.findTotalQuantityByPId(product.getId()).orElse(0);
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setTotalQuantityOrder(totalQuantity);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return totalQuantity;
    }

    public String updateCategory(Product product) {
        String category = product.getClass().getSimpleName();
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setCategory(category);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return category;
    }

    @Transactional
    public boolean apply(Product product) {
        this.updateReviewTotal(product);
        this.updateProductStar(product);
        this.updateTotalQuantityOrder(product);
        this.updateCategory(product);
        return true;
    }

    @Override
    public List<Long> getIdTopBird() {
        PageRequest page = PageRequest.of(0, PagingAndSorting.DEFAULT_PAGE_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "star")
                        .and(Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "totalQuantityOrder")));
        var listsProductSummary = productSummaryRepository.
                findByCategory(new Bird().getClass().getSimpleName(), page);
        if (listsProductSummary.isPresent()) {
            List<Long> listIdTopBird = listsProductSummary.get().stream()
                    .map(productSummary -> productSummary.getProduct().getId()).toList();
            return listIdTopBird;
        }

        return null;
    }

    @Override
    public List<Long> getIdTopAccessories() {
        PageRequest page = PageRequest.of(0, PagingAndSorting.DEFAULT_PAGE_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "star")
                        .and(Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "totalQuantityOrder")));
        var listsProductSummary = productSummaryRepository.
                findByCategory(new Accessory().getClass().getSimpleName(), page);
        if (listsProductSummary.isPresent()) {
            List<Long> listIdTopAccessories = listsProductSummary.get().stream()
                    .map(productSummary -> productSummary.getProduct().getId()).toList();
            return listIdTopAccessories;
        }
        return null;
    }

    @Override
    public List<Long> getIdTopFood() {
        PageRequest page = PageRequest.of(0, PagingAndSorting.DEFAULT_PAGE_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "star")
                        .and(Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "totalQuantityOrder")));
        var listsProductSummary = productSummaryRepository.
                findByCategory(new Food().getClass().getSimpleName(), page);
        if (listsProductSummary.isPresent()) {
            List<Long> listIdTopFood = listsProductSummary.get().stream()
                    .map(productSummary -> productSummary.getProduct().getId()).toList();
            return listIdTopFood;
        }

        return null;
    }

    @Override
    public double CalculationRating(List<OrderDetail> orderDetails) {
        if (orderDetails != null && orderDetails.size() != 0) {
            List<Long> orderDetailId = orderDetails.stream().map(id -> id.getId()).collect(Collectors.toList());
            List<Review> listReview = reviewRepository.findAllByOrderDetailIdIn(orderDetailId).get();
            if (listReview != null && listReview.size() != 0) {
                double sumRating = listReview.stream()
                        .map(rating -> rating.getRating().ordinal() + 1)
                        .reduce(0, Integer::sum);
                return Math.round((sumRating / listReview.size()) * 10.0) / 10.0;
            }
        }
        return 0;
    }

}
