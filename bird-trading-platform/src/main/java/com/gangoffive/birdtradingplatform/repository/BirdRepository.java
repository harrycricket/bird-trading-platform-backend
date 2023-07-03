
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

/**
 * @author Admins
 */

import com.gangoffive.birdtradingplatform.entity.Bird;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
            "WHERE b.name LIKE %?1% " +
            "AND b.type_id IN ?2 " +
            "AND ps.star >= ?3 " +
            "AND ps.discounted_price >= ?4 " +
            "AND ps.discounted_price <= ?5 " +
            "And b.status = 'ACTIVE' " +
            "And b.quantity > 0 ", nativeQuery = true)
    Page<Long> idFilter(String name, List<Long> listType, double star,
                        double lowestPrice, double highestPrice, Pageable pageable);

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
            "AND b.is_deleted = 0 " +
            "AND b.quantity > 0 "
            ,
            nativeQuery = true)
    Page<Long> idFilterShop(Long idShop, String name, List<Long> listType, double star,
                            double lowestPrice, double highestPrice, Pageable pageable);

    Optional<Page<Product>> findByShopOwner_IdAndStatusIn(long id, List<ProductStatus> productStatuses, Pageable pageable);

    Optional<Page<Bird>> findAllByShopOwner_IdAndStatusIn(
            Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findByIdAndShopOwner_IdAndStatusIn(
            Long birdId, Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByNameLikeAndShopOwner_IdAndStatusIn(
            String name, Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByShopOwner_IdAndTypeBird_IdInAndStatusIn(
            Long shopOwnerId, List<Long> typeBirdIds, List<ProductStatus> productStatuses, Pageable pageable
    );


    Optional<Page<Bird>> findAllByShopOwner_IdAndPriceGreaterThanEqualAndStatusIn(
            Long shopOwnerId, double price, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByShopOwner_IdAndProductSummary_DiscountedPriceGreaterThanEqualAndStatusIn(
            Long shopOwnerId, double discountedPrice, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Bird>> findAllByShopOwner_IdAndStatus(
            Long shopOwnerId, ProductStatus productStatus, Pageable pageable
    );

    Bird findByIdAndShopOwner(Long id, ShopOwner shopOwner);
}
