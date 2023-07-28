/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Promotion;
import com.gangoffive.birdtradingplatform.enums.PromotionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Admins
 */
@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Page<Promotion> findAll(Pageable pageable);

    Optional<Page<Promotion>> findById(Long id, Pageable pageable);

    Optional<Page<Promotion>> findAllByNameLike(String name, Pageable pageable);

    Optional<Page<Promotion>> findAllByDescriptionLike(String description, Pageable pageable);

    Optional<Page<Promotion>> findAllByDiscountGreaterThanEqual(double discount, Pageable pageable);

    Optional<Page<Promotion>> findAllByMinimumOrderValueGreaterThanEqual(double minimumOrderValue, Pageable pageable);

    Optional<Page<Promotion>> findAllByUsageLimitGreaterThanEqual(int usageLimit, Pageable pageable);

    Optional<Page<Promotion>> findAllByUsedGreaterThanEqual(int used, Pageable pageable);

    Optional<Page<Promotion>> findAllByTypeIn(List<PromotionType> promotionTypes, Pageable pageable);

    Optional<Page<Promotion>> findAllByStartDateGreaterThanEqual(Date starDate, Pageable pageable);

    Optional<Page<Promotion>> findAllByStartDateBetween(Date from, Date to, Pageable pageable);

    Optional<Page<Promotion>> findAllByEndDateGreaterThanEqual(Date endDate, Pageable pageable);

    Optional<Page<Promotion>> findAllByEndDateBetween(Date from, Date to, Pageable pageable);
}
