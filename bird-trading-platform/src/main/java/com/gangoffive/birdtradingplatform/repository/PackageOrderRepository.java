package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.PackageOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageOrderRepository extends JpaRepository<PackageOrder, Long> {
}
