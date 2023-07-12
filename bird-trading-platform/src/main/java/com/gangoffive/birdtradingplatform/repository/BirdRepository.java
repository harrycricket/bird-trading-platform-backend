
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

/**
 * @author Admins
 */

import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {
    Optional<List<Bird>> findByNameLike(String name);

    @Query(value = "SELECT b.product_id " +
            "FROM `bird-trading-platform`.tbl_bird b " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON b.product_id = ps.product_id " +
            "WHERE (MATCH(b.name) AGAINST (?1 IN NATURAL LANGUAGE MODE) OR b.name LIKE %?1%)" +
//            "AND (COALESCE(?2 ,b.type_id ) = b.type_id OR ?2 IS NULL) " +
            "AND (b.type_id IN (?2) OR ?7 IS NULL) " +
            "AND ps.star >= ?3 " +
            "AND ps.discounted_price >= ?4 " +
            "AND ps.discounted_price <= ?5 " +
            "AND (COALESCE(?6, b.shop_id) = b.shop_id OR ?6 IS NULL) " +
            "AND b.status = 'ACTIVE' " +
            "AND b.quantity > 0 ", nativeQuery = true)
    Page<Long> idFilter(String name,  List<Long> listType, double star,
                        double lowestPrice, double highestPrice, Long shopId, Long typeId ,Pageable pageable);

    Page<Bird> findAllByQuantityGreaterThanAndStatusIn(int quantity, List<ProductStatus> productStatuses, Pageable pageable);

    Optional<Page<Product>> findByShopOwner_Id(long id, Pageable pageable);

    @Query(value = "SELECT b.product_id " +
            "FROM `bird-trading-platform`.tbl_bird b " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON b.product_id = ps.product_id " +
            "WHERE b.shop_id = ?1 " +
            "And b.name LIKE %?2% " +
            "AND b.type_id IN ?3 " +
            "AND ps.star >= ?4 " +
            "AND ps.discounted_price >= ?5 " +
            "AND ps.discounted_price <= ?6 " +
            "And b.status = 'ACTIVE' " +
            "AND b.quantity > 0 "
            ,
            nativeQuery = true)
    Page<Long> idFilterShop(Long idShop, String name, List<Long> listType, double star,
                            double lowestPrice, double highestPrice, Pageable pageable);

    Optional<Page<Product>> findByShopOwner_IdAndStatusIn(long id, List<ProductStatus> productStatuses, Pageable pageable);

    Optional<Page<Bird>> findAllByShopOwner_IdAndStatusIn(
            Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByStatusIn(
            List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findByIdAndShopOwner_IdAndStatusIn(
            Long birdId, Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findByIdAndStatusIn(
            Long birdId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByNameLikeAndShopOwner_IdAndStatusIn(
            String name, Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByNameLikeAndStatusIn(
            String name, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByShopOwner_IdAndTypeBird_IdInAndStatusIn(
            Long shopOwnerId, List<Long> typeBirdIds, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByTypeBird_IdInAndStatusIn(
            List<Long> typeBirdIds, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByShopOwner_IdAndPriceGreaterThanEqualAndStatusIn(
            Long shopOwnerId, double price, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByPriceGreaterThanEqualAndStatusIn(
            double price, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByShopOwner_IdAndProductSummary_DiscountedPriceGreaterThanEqualAndStatusIn(
            Long shopOwnerId, double discountedPrice, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByProductSummary_DiscountedPriceGreaterThanEqualAndStatusIn(
            double discountedPrice, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByShopOwner_IdAndStatus(
            Long shopOwnerId, ProductStatus productStatus, Pageable pageable
    );

    Bird findByIdAndShopOwner(Long id, ShopOwner shopOwner);

    @Query("SELECT b FROM Bird b WHERE b.id IN " +
            "(SELECT DISTINCT b2.id FROM Bird b2 JOIN b2.productSummary ps JOIN b2.tags t " +
            "WHERE b2.typeBird.id = :typeId OR t.id IN :tagIds OR TRUE = TRUE AND b2.quantity > 0 AND b2.status IN :status) " +
            "ORDER BY b.productSummary.totalQuantityOrder DESC")
    List<Product> findDistinctBirdsByTypeAndTagsSortedByTotalQuantity(@Param("typeId") long typeId,
                                                                      @Param("tagIds") List<Long> tagIds,
                                                                      @Param("status") List<ProductStatus> statusList,
                                                                      Pageable pageable);


}
