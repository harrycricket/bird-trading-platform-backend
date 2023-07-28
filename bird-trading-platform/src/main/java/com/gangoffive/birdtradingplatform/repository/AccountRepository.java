package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.enums.UserRole;
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

    @Query(value = "SELECT a.id FROM tblAccount a where a.status = 'ACTIVE'")
    Optional<List<Long>> findAllAccountIdInActive();

    boolean existsByEmail(String email);

    Optional<Account> findByShopOwner_Id(long id);

    Optional<Page<Account>> findAllByRoleIn(List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByIdAndRoleIn(Long accountId, List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByEmailLikeAndRoleIn(String email, List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByFullNameLikeAndRoleIn(String fullName, List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByPhoneNumberLikeAndRoleIn(String phone, List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByAddress_AddressLikeAndRoleIn(String address, List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByStatusInAndRoleIn(List<AccountStatus> statuses, List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByCreatedDateGreaterThanEqualAndRoleIn(Date dateFrom, List<UserRole> userRole, Pageable pageable);

    Optional<Page<Account>> findByCreatedDateBetweenAndRoleIn(Date dateFrom, Date dateTo, List<UserRole> userRole, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "UPDATE tblAccount a SET a.status = ?1 WHERE a.id in ?2")
    int updateListAccountStatus(AccountStatus accountStatus, List<Long> ids);
}
