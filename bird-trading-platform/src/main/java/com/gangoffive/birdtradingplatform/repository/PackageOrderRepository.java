package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.PackageOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PackageOrderRepository extends JpaRepository<PackageOrder, Long> {
    Optional<Page<PackageOrder>> findAllByAccount(Account account, Pageable pageable);

    @Query(value = "SELECT DISTINCT pc.account.id " +
            "FROM tblPackage_Order pc " +
            "JOIN tblOrder o ON pc.id = o.packageOrder.id " +
            "WHERE o.id IN ?1", nativeQuery = false)
    Optional<List<Long>> findAllAccountIdByOrderIds( List<Long> orderIds);
}
