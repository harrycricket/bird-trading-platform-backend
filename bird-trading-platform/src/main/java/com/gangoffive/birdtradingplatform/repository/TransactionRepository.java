package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Transaction;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Optional<Transaction> findByPaypalId(String id);

    Optional<List<Transaction>> findAllByOrder_IdInAndOrder_Status(List<Long> ids, OrderStatus orderStatus);
}
