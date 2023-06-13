package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.OrderDetail;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>{
    @Query(value = "select sum(quantity) as total_quantity_order from tbl_order_detail where product_id = ?", nativeQuery = true)
    Optional<Integer> findTotalQuantityByPId(Long productId);

    List<OrderDetail> findOrderDetailByOrderIn(List<Order> orders);
}
