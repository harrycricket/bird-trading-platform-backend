package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Tag;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TagService {
    List<Tag> getAllTags();

    ResponseEntity<?> addNewTag(String name);

    ResponseEntity<?> getAllTagByShopOwnerId(Long shopId);
}
