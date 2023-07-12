package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Account> findByShopOwner_Id(long id);

    Optional<Page<Account>> findById(Long accountId, Pageable pageable);

    Optional<Page<Account>> findByEmailLike(String email, Pageable pageable);

    Optional<Page<Account>> findByFullNameLike(String fullName, Pageable pageable);

    Optional<Page<Account>> findByPhoneNumberLike(String phone, Pageable pageable);

    Optional<Page<Account>> findByAddress_AddressLike(String address, Pageable pageable);

    Optional<Page<Account>> findByStatusIn(List<AccountStatus> statuses, Pageable pageable);

    Optional<Page<Account>> findByCreatedDateGreaterThanEqual(Date dateFrom, Pageable pageable);

    Optional<Page<Account>> findByCreatedDateBetween(Date dateFrom, Date dateTo, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblAccount a SET a.status = ?1 WHERE a.id in ?2")
    int updateListAccountStatus(AccountStatus accountStatus, List<Long> ids);
}
