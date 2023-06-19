package com.gangoffive.birdtradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Transaction;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>{
    Optional<Transaction> findByPaypalId(String id);
}
