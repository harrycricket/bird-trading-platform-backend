package com.gangoffive.birdtradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Address;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long>{

}
