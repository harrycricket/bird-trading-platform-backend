package com.gangoffive.birdtradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>{

}
