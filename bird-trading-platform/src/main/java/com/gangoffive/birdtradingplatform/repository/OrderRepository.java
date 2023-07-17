package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
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

    Optional<Order> findByShopOwnerAndId(ShopOwner shopOwner, Long id);

    List<Order> findByShopOwnerAndCreatedDateBetween(ShopOwner shopOwner, Date dateFrom, Date dateTo);

    List<Order> findAllByCreatedDateBetween(Date dateFrom, Date dateTo);

    Optional<List<Order>> findAllByPackageOrder_IdAndPackageOrder_Account(Long id, Account account);

    Optional<Page<Order>> findByShopOwner_Id(Long id, Pageable pageable);

    // Query to check if all order IDs are present in a specific shop ID
    @Query("SELECT COUNT(o.id) = :orderCount FROM tblOrder o WHERE o.shopOwner.id = :shopId AND o.id IN :orderIds")
    boolean checkIfOrderIdsBelongToShopId(List<Long> orderIds, Long shopId, int orderCount);

    @Modifying
    @Transactional
    @Query(value = "Update tblOrder o Set o.status = ?1 Where o.id In ?2")
    int updateStatusOfListId(OrderStatus orderStatus, List<Long> ids);

    Optional<Page<Order>> findByIdAndShopOwner_IdAndStatusIn(
            Long orderId, Long shopId, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findByIdAndStatusIn(
            Long orderId, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndStatusIn(
            Long shopId, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndPackageOrder_PaymentMethodInAndStatusIn(
            Long shopId, List<PaymentMethod> paymentMethods, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByPackageOrder_PaymentMethodInAndStatusIn(
            List<PaymentMethod> paymentMethods, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findByIdIn(
            List<Long> orderIds, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndTotalPriceGreaterThanEqualAndStatusIn(
            Long shopId, double totalPrice, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByTotalPriceGreaterThanEqualAndStatusIn(
            double totalPrice, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndShippingFeeGreaterThanEqualAndStatusIn(
            Long shopId, double totalPrice, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShippingFeeGreaterThanEqualAndStatusIn(
            double totalPrice, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndCreatedDateGreaterThanEqualAndStatusIn(
            Long shopId, Date dateFrom, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByCreatedDateGreaterThanEqualAndStatusIn(
            Date dateFrom, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndCreatedDateBetweenAndStatusIn(
            Long shopId, Date dateFrom, Date dateTo, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByCreatedDateBetweenAndStatusIn(
            Date dateFrom, Date dateTo, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndLastedUpdateGreaterThanEqualAndStatusIn(
            Long shopId, Date dateFrom, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByLastedUpdateGreaterThanEqualAndStatusIn(
            Date dateFrom, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_IdAndLastedUpdateBetweenAndStatusIn(
            Long shopId, Date dateFrom, Date dateTo, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByLastedUpdateBetweenAndStatusIn(
            Date dateFrom, Date dateTo, List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByStatusIn(
            List<OrderStatus> orderStatuses, Pageable pageable
    );

    Optional<Page<Order>> findAllByShopOwner_Id(Long shopId, Pageable pageable);

    Optional<Page<Order>> findAllByPackageOrder_Id(Long packageOrderId, Pageable pageable);

    Optional<List<Order>> findAllByPackageOrder_Id(Long packageOrderId);

    List<Order> findAllByStatusInAndIdIn(List<OrderStatus> orderStatuses, List<Long> listId);
}
