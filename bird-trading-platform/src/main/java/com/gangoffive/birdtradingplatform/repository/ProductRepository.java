package com.gangoffive.birdtradingplatform.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Product;

@Repository
@Primary
public interface ProductRepository extends JpaRepository<Product, Long>{
	
}
