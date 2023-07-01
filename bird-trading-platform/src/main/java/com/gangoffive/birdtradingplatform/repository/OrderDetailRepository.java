package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Order;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.OrderDetail;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
    @Query(value = "select sum(quantity) as total_quantity_order from tbl_order_detail where product_id = ?", nativeQuery = true)
    Optional<Integer> findTotalQuantityByPId(Long productId);

    List<OrderDetail> findOrderDetailByOrderIn(List<Order> orders);

    Optional<List<OrderDetail>> findAllByPromotionShopsContainingAndOrder_ShopOwner_IdAndOrder_StatusIn(
            PromotionShop promotionShop, Long shopId, List<OrderStatus> orderStatuses
    );

    Optional<Page<OrderDetail>> findAllByOrder_ShopOwner(
            ShopOwner shopOwner, Pageable pageable
    );
}
