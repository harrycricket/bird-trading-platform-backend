package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Review;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.ReviewRating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<List<Review>> findAllByOrderDetailIdIn(Iterable<Long> orderDetailIds);

    Optional<List<Review>> findAllByAccountAndOrderDetail_Order_Id(Account account, Long id);

    Optional<Page<Review>> findAllByOrderDetail_Product_Id(Long productId, Pageable pageable);

    Optional<Page<Review>> findAllByOrderDetail_Product_ShopOwner_Id(Long shopId, Pageable pageable);

    Optional<Page<Review>> findByIdAndOrderDetail_Product_ShopOwner_Id(Long reviewId, Long shopId, Pageable pageable);

    Optional<Page<Review>> findByOrderDetail_IdAndOrderDetail_Product_ShopOwner_Id(Long orderDetailId, Long shopId, Pageable pageable);

    Optional<Page<Review>> findAllByAccount_FullNameLikeAndOrderDetail_Product_ShopOwner_Id(String name, Long shopId, Pageable pageable);

    Optional<Page<Review>> findAllByOrderDetail_Product_NameLikeAndOrderDetail_Product_ShopOwner_Id(String name, Long shopId, Pageable pageable);

    Optional<Page<Review>> findAllByRatingGreaterThanEqualAndOrderDetail_Product_ShopOwner_Id(ReviewRating reviewRating, Long shopId, Pageable pageable);

    Optional<Page<Review>> findAllByReviewDateGreaterThanEqualAndOrderDetail_Product_ShopOwner_Id(Date reviewDate, Long shopId, Pageable pageable);

    Optional<Page<Review>> findAllByReviewDateBetweenAndOrderDetail_Product_ShopOwner_Id(Date reviewDateFrom, Date reviewDateTo, Long shopId, Pageable pageable);

    Optional<Review> findByIdAndOrderDetail_Product_ShopOwner_Id(Long reviewId, Long shopId);

    Optional<List<Review>> findAllByReviewDateBetweenAndOrderDetail_Product_ShopOwner(Date dateFrom, Date dateTo, ShopOwner shopOwner);
}
