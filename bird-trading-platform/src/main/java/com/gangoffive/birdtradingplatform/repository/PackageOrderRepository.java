package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.PackageOrder;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface PackageOrderRepository extends JpaRepository<PackageOrder, Long> {
    Optional<Page<PackageOrder>> findAllByAccount(Account account, Pageable pageable);

    @Query(value = "SELECT DISTINCT pc.account.id " +
            "FROM tblPackage_Order pc " +
            "JOIN tblOrder o ON pc.id = o.packageOrder.id " +
            "WHERE o.id IN ?1", nativeQuery = false)
    Optional<List<Long>> findAllAccountIdByOrderIds(List<Long> orderIds);

    Optional<Page<PackageOrder>> findById(Long id, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByAccount_Id(Long accountId, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByPaymentMethodIn(List<PaymentMethod> paymentMethods, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByTransaction_PaypalEmailLike(String payerEmail, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByTransaction_StatusIn(List<TransactionStatus> transactionStatuses, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByTotalPriceGreaterThanEqual(double totalPrice, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByTransaction_TransactionDateGreaterThanEqual(Date dateFrom, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByTransaction_LastedUpdateGreaterThanEqual(Date dateFrom, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByTransaction_TransactionDateBetween(Date dateFrom, Date dateTo, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByTransaction_LastedUpdateBetween(Date dateFrom, Date dateTo, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByShippingAddress_FullNameLike(String fullName, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByShippingAddress_PhoneLike(String phone, Pageable pageable);

    Optional<Page<PackageOrder>> findAllByShippingAddress_AddressLike(String address, Pageable pageable);
}
