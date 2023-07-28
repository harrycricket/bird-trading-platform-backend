/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Admins
 */
@Repository
public interface PromotionShopRepository extends JpaRepository<PromotionShop, Long>{

    @Query(value = "SELECT ps.* FROM `bird-trading-platform`.tbl_promotion_shop ps WHERE ps.end_date > now() and ps.shop_id = ?1", nativeQuery = true)
    Optional<List<PromotionShop>> findAllByShopOwner_Id(long shopId);

    @Query(value = "SELECT ps.* FROM `bird-trading-platform`.tbl_promotion_shop ps" +
                                        " WHERE ps.promotion_s_id IN (" +
                                                "SELECT dps.promotion_s_id " +
                                                "FROM `bird-trading-platform`.tbl_order_detail_promotion_shop dps " +
                                                "WHERE dps.order_d_id = :orderDetailId" +
                                        ")", nativeQuery = true)
    Optional<List<PromotionShop>> findAllByOrderDetail(@Param("orderDetailId") Long orderDetailId);

    @Query(value = "SELECT ps.* FROM tbl_promotion_shop ps JOIN tbl_product_promotion p " +
            "ON ps.promotion_s_id = p.promotion_s_id WHERE p.product_id = ?1 AND ps.end_date >= ?2",
            nativeQuery = true)
    Optional<List<PromotionShop>> findByProductIdAndEndDate(Long productId,  Date endDate);

    @Query(value = "SELECT ps FROM PromotionShop ps JOIN ps.products p where p.id = ?1" , nativeQuery = true)
    Optional<List<PromotionShop>> findByProductIdAndEndDateTest(Long productId);

    @Query(value = "SELECT * FROM `bird-trading-platform`.tbl_promotion_shop WHERE promotion_s_id IN (?1);", nativeQuery = true)
    List<PromotionShop> findAllPromotionShopByIdIn(List<Long> ids);

    @Query(value = "SELECT p.id FROM PromotionShop p where p.endDate = ?1")
    Optional<List<Long>> findAllListPromotionByEndDate(Date endDate );
}
