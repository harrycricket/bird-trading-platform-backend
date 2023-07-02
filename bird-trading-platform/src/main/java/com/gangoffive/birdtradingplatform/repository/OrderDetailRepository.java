package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.OrderDetail;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
    @Query(value = "select sum(quantity) as total_quantity_order from tbl_order_detail where product_id = ?", nativeQuery = true)
    Optional<Integer> findTotalQuantityByPId(Long productId);

    List<OrderDetail> findOrderDetailByOrderIn(List<Order> orders);

    Optional<List<OrderDetail>> findAllByPromotionShopsContainingAndOrder_ShopOwner_IdAndOrder_StatusIn(
            PromotionShop promotionShop, Long shopId, List<OrderStatus> orderStatuses
    );

    Optional<Page<OrderDetail>> findAllByOrder_ShopOwner_Id(
            Long shopOwner, Pageable pageable
    );


    Optional<Page<OrderDetail>> findByIdAndOrder_ShopOwner_Id(
            Long orderDetailId, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByOrder_IdAndOrder_ShopOwner_Id(
            Long orderId, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByProduct_IdAndOrder_ShopOwner_Id(
            Long productId, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByProduct_NameLikeAndOrder_ShopOwner_Id(
            String productName, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByPriceGreaterThanEqualAndOrder_ShopOwner_Id(
            double price, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByProductPromotionRateGreaterThanEqualAndOrder_ShopOwner_Id(
            double promotionRate, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByReview_RatingStarGreaterThanEqualAndOrder_ShopOwner_Id(
            int star, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByOrder_CreatedDateGreaterThanEqualAndOrder_ShopOwner_Id(
            Date dateFrom, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByOrder_CreatedDateBetweenAndOrder_ShopOwner_Id(
            Date dateFrom, Date dateTo, Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderShopOwnerIdAndSortByReviewRatingASC(
            @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderShopOwnerIdAndSortByReviewRatingDESC(
            @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.order_id = :orderId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderIdAndShopOwnerIdAndSortByReviewRatingASC(
            @Param("orderId") Long orderId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.order_id = :orderId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderIdAndShopOwnerIdAndSortByReviewRatingDESC(
            @Param("orderId") Long orderId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_id = :productId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductIdAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("productId") Long productId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_id = :productId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductIdAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("productId") Long productId, @Param("shopId") Long shopId, Pageable pageable
    );

    List<OrderDetail> findAllByProduct_NameLikeAndOrderShopOwner_Id(String productName, Long shopId);

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.order_d_id IN :orderDetailsId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderDetailIdInAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("orderDetailsId") List<Long> orderDetailsId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.order_d_id IN :orderDetailsId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderDetailIdInAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("orderDetailsId") List<Long> orderDetailsId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.price >= :price\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByPriceGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("price") double price, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.price >= :price\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByPriceGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("price") double price, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_promotion_rate >= :promotionRate\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductPromotionRateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("promotionRate") double promotionRate, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_promotion_rate >= :promotionRate\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductPromotionRateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("promotionRate") double promotionRate, @Param("shopId") Long shopId, Pageable pageable
    );

    List<OrderDetail> findAllByReview_RatingStarGreaterThanEqualAndOrder_ShopOwner_Id(
            int star, Long shopId
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("dateFrom") Date dateFrom, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("dateFrom") Date dateFrom, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom AND o.created_date <= :dateTo\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END ASC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateBetweenAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(\n" +
            "\tSELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "\t\t\t\tON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom AND o.created_date <= :dateTo\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN 'NO_STAR' THEN 0\n" +
            "    WHEN 'ONE_STAR' THEN 1\n" +
            "    WHEN 'TWO_STAR' THEN 2\n" +
            "    WHEN 'THREE_STAR' THEN 3\n" +
            "    WHEN 'FOUR_STAR' THEN 4\n" +
            "    WHEN 'FIVE_STAR' THEN 5\n" +
            "    ELSE NULL\n" +
            "END DESC;", nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateBetweenAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("shopId") Long shopId, Pageable pageable
    );

}
