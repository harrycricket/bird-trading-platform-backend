package com.gangoffive.birdtradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.ShopOwner;

@Repository
public interface ShopOwnerRepository extends JpaRepository<ShopOwner, Long>{

}
