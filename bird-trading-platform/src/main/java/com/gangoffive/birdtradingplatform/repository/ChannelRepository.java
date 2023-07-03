package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Channel;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
    Optional<Channel> findChannelByAccount_IdAndShopOwner_Id(long accountId, long shopId);

    Page<Channel> findByShopOwner_Id(long shopId, Pageable pageable);

    Page<Channel> findByAccount_Id(long userId, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "Update tblChannel c Set c.lastedUpdate = ?1 Where c.id = ?2")
    int updateLastedUpdate (Date lastedUpdate, long id);

    @Query(value = "Select c.shopOwner.id From tblChannel  c where c.account.id = ?1 ")
    Page<Long> findListShopIdByUserId(long userId, Pageable pageable);
}
