package com.gangoffive.birdtradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.gangoffive.birdtradingplatform.entity.Product;

@NoRepositoryBean
public interface ProductRepository extends JpaRepository<Product, Long>{
	
}
