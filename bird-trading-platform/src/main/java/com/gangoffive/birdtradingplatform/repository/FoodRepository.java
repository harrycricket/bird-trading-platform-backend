
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Food;
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
public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<List<Food>> findByNameLike(String name);
    @Query(value = "SELECT f.product_id " +
            "FROM `bird-trading-platform`.tbl_food f " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON f.product_id = ps.product_id " +
            "WHERE f.name LIKE %?1% " +
            "AND f.type_id IN (?2) " +
            "AND ps.star >= ?3 " +
            "AND f.price >= ?4 " +
            "AND f.price <= ?5 " +
            "And f.status = 'ACTIVE' " +
            "And f.quantity > 0 ", nativeQuery = true)
    Page<Long> idFilter(String name, List<Long> listTypeId, double star,
                        double lowestPrice, double hightPrice, Pageable pageable);
    Page<Food> findAllByQuantityGreaterThanAndStatusIn(int quantity, List<ProductStatus> productStatuses, Pageable pageable);

    Optional<Page<Product>> findByShopOwner_Id(long id, Pageable pageable);
    @Query(value = "SELECT f.product_id " +
            "FROM `bird-trading-platform`.tbl_food f " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON f.product_id = ps.product_id " +
            "WHERE f.shop_id = ?1 " +
            "AND f.name LIKE %?2% " +
            "AND f.type_id IN (?3) " +
            "AND ps.star >= ?4 " +
            "AND f.price >= ?5 " +
            "AND f.price <= ?6 " +
            "And f.status = 'ACTIVE' ", nativeQuery = true)
    Page<Long> idFilterShop(Long idShop, String name, List<Long> listTypeId, double star,
                        double lowestPrice, double highestPrice, Pageable pageable);
    Optional<Page<Product>> findByShopOwner_IdAndStatusIn(long id, List<ProductStatus> productStatuses, Pageable pageable);
}
