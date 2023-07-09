
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Food;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
            "WHERE (MATCH(f.name) AGAINST (?1 IN NATURAL LANGUAGE MODE) OR f.name LIKE %?1%)" +
            "AND (COALESCE(?2, f.type_id) = f.type_id OR ?2 IS NULL) " +
            "AND ps.star >= ?3 " +
            "AND ps.discounted_price >= ?4 " +
            "AND ps.discounted_price <= ?5 " +
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
            "AND ps.discounted_price >= ?5 " +
            "AND ps.discounted_price <= ?6 " +
            "AND f.quantity > 0 " +
            "And f.status = 'ACTIVE' ", nativeQuery = true)
    Page<Long> idFilterShop(Long idShop, String name, List<Long> listTypeId, double star,
                            double lowestPrice, double highestPrice, Pageable pageable);

    Optional<Page<Product>> findByShopOwner_IdAndStatusIn(long id, List<ProductStatus> productStatuses, Pageable pageable);

    Optional<Page<Food>> findAllByShopOwner_IdAndStatusIn(
            Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByStatusIn(
            List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findByIdAndShopOwner_IdAndStatusIn(
            Long foodId, Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findByIdAndStatusIn(
            Long foodId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByNameLikeAndShopOwner_IdAndStatusIn(
            String name, Long shopId, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByNameLikeAndStatusIn(
            String name, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByShopOwner_IdAndTypeFood_IdInAndStatusIn(
            Long shopOwnerId, List<Long> typeFoodIds, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByTypeFood_IdInAndStatusIn(
            List<Long> typeFoodIds, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByShopOwner_IdAndPriceGreaterThanEqualAndStatusIn(
            Long shopOwnerId, double price, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByPriceGreaterThanEqualAndStatusIn(
            double price, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByShopOwner_IdAndProductSummary_DiscountedPriceGreaterThanEqualAndStatusIn(
            Long shopOwnerId, double discountedPrice, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByProductSummary_DiscountedPriceGreaterThanEqualAndStatusIn(
            double discountedPrice, List<ProductStatus> productStatuses, Pageable pageable
    );

    Optional<Page<Food>> findAllByShopOwner_IdAndStatus(
            Long shopOwnerId, ProductStatus productStatus, Pageable pageable
    );

    Food findByIdAndShopOwner(Long id, ShopOwner shopOwner);

    @Query("SELECT b FROM Food b WHERE b.id IN " +
            "(SELECT DISTINCT b2.id FROM Food b2 JOIN b2.productSummary ps JOIN b2.tags t " +
            "WHERE b2.typeFood.id = :typeId OR t.id IN :tagIds OR TRUE = TRUE AND b2.quantity > 0 AND b2.status IN :status) " +
            "ORDER BY b.productSummary.totalQuantityOrder DESC")
    List<Product> findDistinctBirdsByTypeAndTagsSortedByTotalQuantity(@Param("typeId") long typeId,
                                                                      @Param("tagIds") List<Long> tagIds,
                                                                      @Param("status") List<ProductStatus> statusList,
                                                                      Pageable pageable);
}
