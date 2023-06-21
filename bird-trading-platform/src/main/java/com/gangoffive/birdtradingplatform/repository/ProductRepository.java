package com.gangoffive.birdtradingplatform.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public interface ProductRepository extends JpaRepository<Product, Long>{
	Optional<List<Product>> findByNameLike(String name);
	Optional<Page<Product>> findByShopOwner_Id(long id, Pageable pageable);

	@Query(value = "SELECT p FROM Product p where p.quantity > 0 and p.id = ?1")
	Optional<Product> findByIdWithCondition(long id);

}
