
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Food;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
    Optional<List<Food>> findByNameLike(String name);
    @Query(value = "SELECT product_id FROM `bird-trading-platform`.tbl_food where type_id = ?;", nativeQuery =true)
    List<Long> findType(Long idtype);
    @Query(value = "SELECT type_id FROM `bird-trading-platform`.tbl_food;", nativeQuery = true)
    List<Long> allIdType();

    @Query(value = "SELECT f.product_id " +
            "FROM `bird-trading-platform`.tbl_food f " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON f.product_id= ps.product_id " +
            "where f.name LIKE %?1% " +
            "And f.type_id in (?2) " +
            "And ps.star >= ?3 " +
            "And f.price >= ?4 " +
            "And f.price <= ?5", nativeQuery = true)
    List<Long> idFilter(String name, List<Long> listTypeId, double star,
                        double lowestPrice, double hightPrice, Pageable pageable);
}
