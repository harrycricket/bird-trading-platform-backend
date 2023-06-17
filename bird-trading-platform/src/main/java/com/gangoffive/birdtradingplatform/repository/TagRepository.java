package com.gangoffive.birdtradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gangoffive.birdtradingplatform.entity.Tag;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>{
    List<Tag> findByNameIn(List<String> names);
}
