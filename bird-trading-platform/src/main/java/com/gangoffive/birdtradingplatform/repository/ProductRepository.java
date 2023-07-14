package com.gangoffive.birdtradingplatform.repository;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import com.gangoffive.birdtradingplatform.enums.ShopOwnerStatus;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<List<Product>> findByNameLike(String name);

    Optional<Page<Product>> findByShopOwner_IdAndStatusIn(long id, List<ProductStatus> productStatuses, Pageable pageable);
    Integer countAllByShopOwner_IdAndStatusIn(Long id, List<ProductStatus> productStatuses);

    Optional<List<Product>> findByIdInAndQuantityGreaterThanAndStatusInAndShopOwner_StatusIn(List<Long> ids, int quantity,
                                                                                            List<ProductStatus> productStatuses, List<ShopOwnerStatus> shopOwnerStatuses);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Product p SET p.status = ?1 WHERE p.id in ?2")
    int updateListProductStatus(ProductStatus productStatus, List<Long> ids);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Product p SET p.quantity = ?1 WHERE p.id = ?2 AND p.shopOwner.id = ?3 AND p.status = 'ACTIVE' ")
    int updateListProductQuantity(Integer quantity, Long id, Long shopId);

    List<Product> findByStatusIn(List<ProductStatus> productStatuses);

    Optional<Product> findByIdAndStatusInAndShopOwner_Id(long productId, List<ProductStatus> productStatuses, long shopId);

}
