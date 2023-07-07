package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<List<Review>> findAllByOrderDetailIdIn(Iterable<Long> orderDetailIds);

    Optional<List<Review>> findAllByAccountAndOrderDetail_Order_Id(Account account, Long id);

    Optional<Page<Review>> findAllByOrderDetail_Product_Id(Long productId, Pageable pageable);
}
