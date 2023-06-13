
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Accessory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccessoryRepository extends JpaRepository<Accessory, Long> {
    Optional<List<Accessory>> findByNameLike(String name);
    @Query(value = "SELECT product_id FROM `bird-trading-platform`.tbl_accessory where type_id=?;", nativeQuery =true)
    List<Long> findType(Long idtype);
    @Query(value = "SELECT type_id FROM `bird-trading-platform`.tbl_accessory;", nativeQuery = true)
    List<Long> allIdType();

    @Query(value = "SELECT a.product_id " +
            "FROM `bird-trading-platform`.tbl_accessory a " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON a.product_id= ps.product_id " +
            "where a.name LIKE %?1% " +
            "And a.type_id in (?2) " +
            "And ps.star >= ?3 " +
            "And a.price >= ?4 " +
            "And a.price <= ?5", nativeQuery = true)
    Page<Long> idFilter(String name, List<Long> listTypeId, double star,
                        double lowestPrice, double hightPrice, Pageable pageable);

}
