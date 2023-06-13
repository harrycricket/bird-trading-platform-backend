package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TypeAccessoryRepository extends JpaRepository<TypeAccessory, Long> {

    @Query(value = "Select type_a_id from `bird-trading-platform`.tbl_type_accessory", nativeQuery = true)
    List<Long> findAllId();
}
