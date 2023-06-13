
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

/**
 *
 * @author Admins
 */

import com.gangoffive.birdtradingplatform.dto.ProductFilterDto;
import com.gangoffive.birdtradingplatform.entity.Bird;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BirdRepository extends JpaRepository<Bird, Long> {
    Optional<List<Bird>> findByNameLike(String name);
    @Query(value = "SELECT product_id FROM `bird-trading-platform`.tbl_bird Where type_id= ?;", nativeQuery =true)
    List<Long> findType(Long idtype);

    @Query(value = "SELECT type_id FROM `bird-trading-platform`.tbl_bird;", nativeQuery = true)
    List<Long> allIdType();

    @Query(value = "SELECT b.product_id " +
            "FROM `bird-trading-platform`.tbl_bird b " +
            "INNER JOIN `bird-trading-platform`.tbl_product_summary ps " +
            "ON b.product_id= ps.product_id " +
            "where b.name LIKE %?1% " +
            "And b.type_id in (?2) " +
            "And ps.star >= ?3 " +
            "And b.price >= ?4 " +
            "And b.price <= ?5", nativeQuery = true)
    List<Long> idFilter(String name, List<Long> listTypeId, double star,
                        double lowestPrice, double hightPrice, Pageable pageable);
}
