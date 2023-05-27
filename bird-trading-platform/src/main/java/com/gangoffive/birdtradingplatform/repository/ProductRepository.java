package com.gangoffive.birdtradingplatform.repository;

import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Product;

import java.util.List;
import java.util.Optional;

@Repository
@Primary
public interface ProductRepository extends JpaRepository<Product, Long>{
}
