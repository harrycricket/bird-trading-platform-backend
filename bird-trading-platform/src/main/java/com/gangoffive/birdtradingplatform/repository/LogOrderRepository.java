package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.LogOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogOrderRepository extends JpaRepository<LogOrder, Long> {
}
