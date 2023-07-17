package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.common.PagingAndSorting;
import com.gangoffive.birdtradingplatform.common.ProductStatusConstant;
import com.gangoffive.birdtradingplatform.common.ShopOwnerConstant;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.ProductSummaryService;
import com.gangoffive.birdtradingplatform.service.PromotionPriceService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
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
    private final PromotionPriceService promotionPriceService;

    public double updateProductStar(Product product) {
        double star = this.CalculationRating(product.getOrderDetails());
//        var productSummary = productSummaryRepository.findByProductIdAndProductStatusIn(product.getId(), ProductStatusConstant.LIST_STATUS_GET_FOR_USER).orElse(new ProductSummary());
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
//        var productSummary = productSummaryRepository.findByProductIdAndProductStatusIn(product.getId(), ProductStatusConstant.LIST_STATUS_GET_FOR_USER).orElse(new ProductSummary());
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setReviewTotal(reviewTotal);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return reviewTotal;
    }

    public int updateTotalQuantityOrder(Product product) {
        int totalQuantity = orderDetailRepository.findTotalQuantityByPId(product.getId()).orElse(0);
//        var productSummary = productSummaryRepository.findByProductIdAndProductStatusIn(product.getId(), ProductStatusConstant.LIST_STATUS_GET_FOR_USER).orElse(new ProductSummary());
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setTotalQuantityOrder(totalQuantity);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return totalQuantity;
    }

    public String updateCategory(Product product) {
        String category = product.getClass().getSimpleName();
//        var productSummary = productSummaryRepository.findByProductIdAndProductStatusIn(product.getId(), ProductStatusConstant.LIST_STATUS_GET_FOR_USER).orElse(new ProductSummary());
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setCategory(category);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return category;
    }

    public double discountedPrice(Product product) {
        double discountedPrice = Optional.ofNullable(promotionPriceService.getDiscountedPrice(product)).orElse(0.0);
//        var productSummary = productSummaryRepository.findByProductIdAndProductStatusIn(product.getId(), ProductStatusConstant.LIST_STATUS_GET_FOR_USER).orElse(new ProductSummary());
        var productSummary = productSummaryRepository.findByProductId(product.getId()).orElse(new ProductSummary());
        productSummary.setDiscountedPrice(discountedPrice);
        productSummary.setProduct(product);
        productSummaryRepository.save(productSummary);
        return discountedPrice;
    }

    @Transactional
    public boolean apply(Product product) {
        this.updateReviewTotal(product);
        this.updateProductStar(product);
        this.updateTotalQuantityOrder(product);
        this.updateCategory(product);
        this.discountedPrice(product);
        return true;
    }

    @Override
    public List<Long> getIdTopBird() {
        PageRequest page = PageRequest.of(0, PagingAndSorting.DEFAULT_PAGE_SIZE,
                Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "star")
                        .and(Sort.by(PagingAndSorting.DEFAULT_SORT_DIRECTION, "totalQuantityOrder")));
        var listsProductSummary = productSummaryRepository.
                findByCategoryAndProductQuantityGreaterThanAndDeletedFalseAndProductStatusInAndProduct_ShopOwner_StatusIn(new Bird().getClass().getSimpleName(),
                        ProductStatusConstant.QUANTITY_PRODUCT_FOR_USER, ProductStatusConstant.LIST_STATUS_GET_FOR_USER, ShopOwnerConstant.STATUS_SHOP_PRODUCT_FOR_USER,page);
        if (listsProductSummary.isPresent()) {
            List<Long> listIdTopBird = listsProductSummary.get().stream()
                    .map(proSum -> proSum.getProduct().getId()).toList();
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
                findByCategoryAndProductQuantityGreaterThanAndDeletedFalseAndProductStatusInAndProduct_ShopOwner_StatusIn(new Accessory().getClass().getSimpleName(),
                        ProductStatusConstant.QUANTITY_PRODUCT_FOR_USER, ProductStatusConstant.LIST_STATUS_GET_FOR_USER, ShopOwnerConstant.STATUS_SHOP_PRODUCT_FOR_USER,page);
        if (listsProductSummary.isPresent()) {
            List<Long> listIdTopAccessories = listsProductSummary.get().stream()
                    .map(proSum -> proSum.getProduct().getId()).toList();
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
                findByCategoryAndProductQuantityGreaterThanAndDeletedFalseAndProductStatusInAndProduct_ShopOwner_StatusIn(new Food().getClass().getSimpleName(),
                        ProductStatusConstant.QUANTITY_PRODUCT_FOR_USER, ProductStatusConstant.LIST_STATUS_GET_FOR_USER, ShopOwnerConstant.STATUS_SHOP_PRODUCT_FOR_USER,page);
        if (listsProductSummary.isPresent()) {
            List<Long> listIdTopFood = listsProductSummary.get().stream()
                    .map(proSum -> proSum.getProduct().getId()).toList();
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
                        .map(rating -> rating.getRating().getStar())
                        .reduce(0, Integer::sum);
                return Math.round((sumRating / listReview.size()) * 10.0) / 10.0;
            }
        }
        return 0;
    }

}
