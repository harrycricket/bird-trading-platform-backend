package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
    @Query(value = "select sum(quantity) as total_quantity_order from tbl_order_detail where product_id = ?", nativeQuery = true)
    Optional<Integer> findTotalQuantityByPId(Long productId);

    List<OrderDetail> findOrderDetailByOrderIn(List<Order> orders);

    Optional<OrderDetail> findByIdAndOrder_PackageOrder_Account(Long id, Account account);

    Optional<List<OrderDetail>> findAllByPromotionShopsContainingAndOrder_ShopOwner_IdAndOrder_StatusIn(
            PromotionShop promotionShop, Long shopId, List<OrderStatus> orderStatuses
    );

    Optional<List<OrderDetail>> findAllByPromotionShopsContainingAndOrder_StatusIn(
            PromotionShop promotionShop, List<OrderStatus> orderStatuses
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


    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od INNER JOIN `bird-trading-platform`.tbl_review rv \n" +
            "           ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "           WHERE o.shop_id = :shopId\n" +
            ") AS tmp WHERE \n" +
            "    CASE\n" +
            "        WHEN rating = '5' THEN 5\n" +
            "        WHEN rating = '4' THEN 4\n" +
            "        WHEN rating = '3' THEN 3\n" +
            "        WHEN rating = '2' THEN 2\n" +
            "        WHEN rating = '1' THEN 1\n" +
            "        ELSE 0\n" +
            "END >= :star",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od INNER JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "           ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "           WHERE o.shop_id = :shopId\n" +
                    "           AND CASE\n" +
                    "               WHEN rating = '5' THEN 5\n" +
                    "               WHEN rating = '4' THEN 4\n" +
                    "               WHEN rating = '3' THEN 3\n" +
                    "               WHEN rating = '2' THEN 2\n" +
                    "               WHEN rating = '1' THEN 1\n" +
                    "               ELSE 0\n" +
                    "           END >= :star"
            ,nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByReviewRatingStarGreaterThanEqualAndOrderShopOwnerId(
            @Param("star") int star, @Param("shopId") Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByOrder_CreatedDateGreaterThanEqualAndOrder_ShopOwner_Id(
            Date dateFrom, Long shopId, Pageable pageable
    );

    Optional<Page<OrderDetail>> findAllByOrder_CreatedDateBetweenAndOrder_ShopOwner_Id(
            Date dateFrom, Date dateTo, Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id " +
            "FROM ( " +
            "    SELECT od.*, rv.rating AS rating FROM tbl_order_detail od " +
            "    LEFT JOIN tbl_review rv ON od.order_d_id = rv.order_detail_id " +
            "    JOIN tbl_order o ON od.order_id = o.order_id " +
            "    WHERE o.shop_id = :shopId " +
            ") AS tmp " +
            "ORDER BY CASE " +
            "    WHEN tmp.rating = '1' THEN 1 " +
            "    WHEN tmp.rating = '2' THEN 2 " +
            "    WHEN tmp.rating = '3' THEN 3 " +
            "    WHEN tmp.rating = '4' THEN 4 " +
            "    WHEN tmp.rating = '5' THEN 5 " +
            "    ELSE 0 " +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM tbl_order_detail od " +
                    "                     LEFT JOIN tbl_review rv ON od.order_d_id = rv.order_detail_id " +
                    "                     JOIN tbl_order o ON od.order_id = o.order_id " +
                    "                     WHERE o.shop_id = :shopId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderShopOwnerIdAndSortByReviewRatingASC(
            @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "               WHERE o.shop_id = :shopId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "               WHERE o.shop_id = :shopId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderShopOwnerIdAndSortByReviewRatingDESC(
            @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "               WHERE o.shop_id = :shopId AND o.order_id = :orderId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "               WHERE o.shop_id = :shopId AND o.order_id = :orderId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderIdAndShopOwnerIdAndSortByReviewRatingASC(
            @Param("orderId") Long orderId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.order_id = :orderId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND o.order_id = :orderId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderIdAndShopOwnerIdAndSortByReviewRatingDESC(
            @Param("orderId") Long orderId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_id = :productId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND od.product_id = :productId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductIdAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("productId") Long productId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "               WHERE o.shop_id = :shopId AND od.product_id = :productId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND od.product_id = :productId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductIdAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("productId") Long productId, @Param("shopId") Long shopId, Pageable pageable
    );

    List<OrderDetail> findAllByProduct_NameLikeAndOrder_ShopOwner_Id(String productName, Long shopId);

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.order_d_id IN :orderDetailsId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND od.order_d_id IN :orderDetailsId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderDetailIdInAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("orderDetailsId") List<Long> orderDetailsId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.order_d_id IN :orderDetailsId\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND od.order_d_id IN :orderDetailsId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderDetailIdInAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("orderDetailsId") List<Long> orderDetailsId, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.price >= :price\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND od.price >= :price",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByPriceGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("price") double price, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.price >= :price\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND od.price >= :price",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByPriceGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("price") double price, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_promotion_rate >= :promotionRate\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_promotion_rate >= :promotionRate",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductPromotionRateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("promotionRate") double promotionRate, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND od.product_promotion_rate >= :promotionRate\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND od.product_promotion_rate >= :promotionRate",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByProductPromotionRateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("promotionRate") double promotionRate, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od INNER JOIN `bird-trading-platform`.tbl_review rv \n" +
            "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "               WHERE o.shop_id = :shopId\n" +
            ") AS tmp \n" +
            "WHERE \n" +
            "    CASE\n" +
            "        WHEN rating = '5' THEN 5\n" +
            "        WHEN rating = '4' THEN 4\n" +
            "        WHEN rating = '3' THEN 3\n" +
            "        WHEN rating = '2' THEN 2\n" +
            "        WHEN rating = '1' THEN 1\n" +
            "        ELSE 0\n" +
            "    END >= :star\n" +
            "ORDER BY \n" +
            "    CASE\n" +
            "        WHEN rating = '5' THEN 5\n" +
            "        WHEN rating = '4' THEN 4\n" +
            "        WHEN rating = '3' THEN 3\n" +
            "        WHEN rating = '2' THEN 2\n" +
            "        WHEN rating = '1' THEN 1\n" +
            "        ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od INNER JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "               WHERE o.shop_id = :shopId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByReviewRatingStarGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("star") int star, @Param("shopId") Long shopId, Pageable pageable
    );


    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od INNER JOIN `bird-trading-platform`.tbl_review rv \n" +
            "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "               WHERE o.shop_id = :shopId\n" +
            ") AS tmp \n" +
            "WHERE \n" +
            "    CASE\n" +
            "        WHEN rating = '5' THEN 5\n" +
            "        WHEN rating = '4' THEN 4\n" +
            "        WHEN rating = '3' THEN 3\n" +
            "        WHEN rating = '2' THEN 2\n" +
            "        WHEN rating = '1' THEN 1\n" +
            "        ELSE 0\n" +
            "    END >= :star\n" +
            "ORDER BY \n" +
            "    CASE\n" +
            "        WHEN rating = '5' THEN 5\n" +
            "        WHEN rating = '4' THEN 4\n" +
            "        WHEN rating = '3' THEN 3\n" +
            "        WHEN rating = '2' THEN 2\n" +
            "        WHEN rating = '1' THEN 1\n" +
            "        ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od INNER JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "               ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "               WHERE o.shop_id = :shopId",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByReviewRatingStarGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("star") int star, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("dateFrom") Date dateFrom, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateGreaterThanEqualAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("dateFrom") Date dateFrom, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom AND o.created_date <= :dateTo\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END ASC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom AND o.created_date <= :dateTo",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateBetweenAndOrderShopOwnerIdSortByReviewRatingASC(
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("shopId") Long shopId, Pageable pageable
    );

    @Query(value = "SELECT tmp.order_d_id, tmp.price, tmp.product_promotion_rate, tmp.quantity, tmp.order_id, tmp.product_id FROM\n" +
            "(" +
            "SELECT od.*, rv.rating AS rating FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
            "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
            "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom AND o.created_date <= :dateTo\n" +
            ") AS tmp ORDER BY CASE tmp.rating\n" +
            "    WHEN '1' THEN 1\n" +
            "    WHEN '2' THEN 2\n" +
            "    WHEN '3' THEN 3\n" +
            "    WHEN '4' THEN 4\n" +
            "    WHEN '5' THEN 5\n" +
            "    ELSE 0\n" +
            "END DESC",
            countQuery = "SELECT COUNT(*) FROM `bird-trading-platform`.tbl_order_detail od LEFT JOIN `bird-trading-platform`.tbl_review rv \n" +
                    "                ON od.order_d_id = rv.order_detail_id JOIN `bird-trading-platform`.tbl_order o ON od.order_id = o.order_id\n" +
                    "                WHERE o.shop_id = :shopId AND o.created_date >= :dateFrom AND o.created_date <= :dateTo",
            nativeQuery = true)
    Optional<Page<OrderDetail>> findAllByOrderCreatedDateBetweenAndOrderShopOwnerIdSortByReviewRatingDESC(
            @Param("dateFrom") Date dateFrom, @Param("dateTo") Date dateTo, @Param("shopId") Long shopId, Pageable pageable
    );

}
