package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.TypeBird;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeBirdRepository extends JpaRepository<TypeBird, Long> {
    @Query(value = "Select type_f_id from `bird-trading-platform`.tbl_type_food", nativeQuery = true)
    List<Long> findAllId();
}
