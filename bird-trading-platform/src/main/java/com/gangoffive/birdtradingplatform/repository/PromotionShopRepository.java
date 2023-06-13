/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import org.springframework.data.jpa.repository.JpaRepository;
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
    
}
