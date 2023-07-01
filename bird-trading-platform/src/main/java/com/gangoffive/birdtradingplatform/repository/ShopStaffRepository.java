package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.dto.ShopStaffDto;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.ShopStaff;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopStaffRepository extends JpaRepository<ShopStaff, Long>{
    Optional<ShopStaff> findByUserName(String userName);
    Page<ShopStaff> findByShopOwner(ShopOwner shopOwner, Pageable pageable);

    @Transactional
    @Modifying
    @Query(value = "Update tblShop_Staff_Acc s Set s.status = ?1 where s.id In ?2 And s.shopOwner.id = ?3")
    int updateStatusWithShopId(AccountStatus accountStatus, List<Long> staffId, long shopId);
}
