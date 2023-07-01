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

    Optional<List<PromotionShop>> findByShopOwner_Id(long shopId);

    @Query(value = "SELECT ps.* FROM tblPromotion_Shop ps " +
            "INNER JOIN tblOrder_Detail od ON ps.promotion_s_id = od.promotion_s_id " +
            "WHERE od.order_detail_id = :orderDetailId", nativeQuery = true)
    Optional<List<PromotionShop>> findAllByOrderDetail(@Param("orderDetailId") Long orderDetailId);
}
