
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.common.ProductStatusConstant;
import com.gangoffive.birdtradingplatform.entity.Accessory;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Long> {
    Optional<List<Accessory>> findByNameLikeAndStatusInAndQuantityGreaterThanEqual(String name, List<ProductStatus> productStatuses, int quantity);
    @Query(value = "SELECT a.product_id " +
            "FROM `bird-trading-platform`.tbl_accessory a " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON a.product_id= ps.product_id " +
            "where a.name LIKE %?1% " +
            "And a.type_id in (?2) " +
            "And ps.star >= ?3 " +
            "And ps.discounted_price >= ?4 " +
            "And ps.discounted_price <= ?5 " +
            "And a.status = 'ACTIVE' " +
            "And a.quantity > 0 ", nativeQuery = true)
    Page<Long> idFilter(String name, List<Long> listTypeId, double star,
                        double lowestPrice, double highestPrice, Pageable pageable);

    Page<Accessory> findAllByQuantityGreaterThanAndStatusIn(int quantity, List<ProductStatus> productStatuses, Pageable pageable);

    Optional<Page<Product>> findByShopOwner_Id(long id, Pageable pageable);

    @Query(value = "SELECT a.product_id " +
            "FROM `bird-trading-platform`.tbl_accessory a " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON a.product_id = ps.product_id " +
            "where a.shop_id = ?1 " +
            "And a.name LIKE %?2% " +
            "And a.type_id in (?3) " +
            "And ps.star >= ?4 " +
            "And ps.discounted_price >= ?5 " +
            "And ps.discounted_price <= ?6 " +
            "And a.quantity > 0 " +
            "And a.status = 'ACTIVE' ", nativeQuery = true)

    Page<Long> idFilterShop(Long idShop, String name, List<Long> listTypeId, double star,
                        double lowestPrice, double highestPrice, Pageable pageable);
    Optional<Page<Product>> findByShopOwner_IdAndStatusIn(long id, List<ProductStatus> productStatuses, Pageable pageable);

}
