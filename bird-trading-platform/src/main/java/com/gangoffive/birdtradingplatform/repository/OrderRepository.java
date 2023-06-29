package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
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
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByShopOwner(ShopOwner shopOwner);

    List<Order> findByShopOwnerAndCreatedDateBetween(ShopOwner shopOwner, Date dateFrom, Date dateTo);

    List<Order> findAllByCreatedDateBetween(Date dateFrom, Date dateTo);

    List<Order> findAllByPackageOrder_Id(Long id);

    Optional<Page<Order>> findByShopOwner(ShopOwner shopOwner, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "Update tblOrder o Set o.status = ?1 Where o.id In ?2")
    int updateStatusOfListId(OrderStatus orderStatus, List<Long> ids);
}
