package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.TypeFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeFoodRepository extends JpaRepository<TypeFood, Long> {
    @Query(value = "Select type_b_id from `bird-trading-platform`.tbl_type_bird", nativeQuery = true)
    List<Long> findAllId();
}
