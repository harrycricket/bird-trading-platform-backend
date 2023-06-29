package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.ProductSummary;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductSummaryRepository extends JpaRepository<ProductSummary,Long>{
    Optional<ProductSummary> findByProductIdAndProductStatusIn(Long productId, List<ProductStatus> productStatuses);
    Optional<ProductSummary> findByProductId(Long id);
    Optional<List<ProductSummary>> findByCategory(String category, Pageable pageable);
    Optional<List<ProductSummary>> findByCategoryAndProductQuantityGreaterThanAndDeletedFalseAndProductStatusIn
                    (String category, int quantity, List<ProductStatus> productStatuses , Pageable pageable);

}
