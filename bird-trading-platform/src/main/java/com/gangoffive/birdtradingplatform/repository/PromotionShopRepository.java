/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author Admins
 */
@Repository
public interface PromotionShopRepository extends JpaRepository<PromotionShop, Long>{

    @Query(value = "SELECT ps.* FROM `bird-trading-platform`.tbl_promotion_shop ps WHERE ps.end_date > now()", nativeQuery = true)
    Optional<List<PromotionShop>> findAllByShopOwner_Id(long shopId);

    @Query(value = "SELECT ps.* FROM `bird-trading-platform`.tbl_promotion_shop ps" +
                                        " WHERE ps.promotion_s_id IN (" +
                                                "SELECT dps.promotion_s_id " +
                                                "FROM `bird-trading-platform`.tbl_order_detail_promotion_shop dps " +
                                                "WHERE dps.order_d_id = :orderDetailId" +
                                        ")", nativeQuery = true)
    Optional<List<PromotionShop>> findAllByOrderDetail(@Param("orderDetailId") Long orderDetailId);
}
