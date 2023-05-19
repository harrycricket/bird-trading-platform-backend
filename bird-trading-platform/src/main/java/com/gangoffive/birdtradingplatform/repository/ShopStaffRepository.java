package com.gangoffive.birdtradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.ShopStaff;

@Repository
public interface ShopStaffRepository extends JpaRepository<ShopStaff, Long>{

}
