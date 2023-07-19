package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.LogOrder;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LogOrderRepository extends JpaRepository<LogOrder, Long> {
    Optional<Page<LogOrder>> findAllByShopStaff_ShopOwner_Id(Long shopId, Pageable pageable);

    Optional<Page<LogOrder>> findByIdAndShopStaff_ShopOwner_Id(Long id, Long shopId, Pageable pageable);

    Optional<Page<LogOrder>> findByOrder_IdAndShopStaff_ShopOwner_Id(Long orderId, Long shopId, Pageable pageable);

    Optional<Page<LogOrder>> findByStatusInAndShopStaff_ShopOwner_Id(List<OrderStatus> orderStatus, Long shopId, Pageable pageable);

    Optional<Page<LogOrder>> findByTimestampGreaterThanEqualAndShopStaff_ShopOwner_Id(Date timestamp, Long shopId, Pageable pageable);

    Optional<Page<LogOrder>> findByTimestampBetweenAndShopStaff_ShopOwner_Id(Date dateFrom, Date dateTo, Long shopId, Pageable pageable);

    Optional<Page<LogOrder>> findByShopStaff_IdAndShopStaff_ShopOwner_Id(Long staffId, Long shopId, Pageable pageable);

    Optional<Page<LogOrder>> findByShopStaff_UserNameLikeAndShopStaff_ShopOwner_Id(String username, Long shopId, Pageable pageable);
}
