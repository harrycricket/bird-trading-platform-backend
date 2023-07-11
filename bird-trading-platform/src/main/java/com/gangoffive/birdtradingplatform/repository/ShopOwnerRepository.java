package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.ShopOwnerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShopOwnerRepository extends JpaRepository<ShopOwner, Long> {
    Optional<ShopOwner> findByAccount_Id(long id);

    Optional<ShopOwner> findByAccount_Email(String email);

    Optional<Page<ShopOwner>> findById(Long shopOwnerId, Pageable pageable);

    Optional<Page<ShopOwner>> findByAccount_EmailLike(String email, Pageable pageable);

    Optional<Page<ShopOwner>> findByShopNameLike(String shopName, Pageable pageable);

    Optional<Page<ShopOwner>> findByShopPhoneLike(String shopPhone, Pageable pageable);

    Optional<Page<ShopOwner>> findByAddress_AddressLike(String address, Pageable pageable);

    Optional<Page<ShopOwner>> findByStatusIn(List<ShopOwnerStatus> shopOwnerStatuses, Pageable pageable);

    Optional<Page<ShopOwner>> findByCreatedDateGreaterThanEqual(Date dateFrom, Pageable pageable);

    Optional<Page<ShopOwner>> findByCreatedDateBetween(Date dateFrom, Date dateTo, Pageable pageable);
}
