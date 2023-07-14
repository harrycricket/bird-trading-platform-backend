package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.dto.TagDto;
import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class TagController {
    private final TagService tagService;

    @GetMapping("shop-owner/tags")
    public List<Tag> getAllTags() {
        return tagService.getAllTags();
    }

    @PostMapping("shop-owner/tag")
    public ResponseEntity<?> addNewTag(@RequestBody TagDto tag) {
        return tagService.addNewTag(tag.getName());
    }

    @GetMapping("/tags/{shopId}")
    public ResponseEntity<?> getAllTagByShopOwnerId(@PathVariable Long shopId) {
        return tagService.getAllTagByShopOwnerId(shopId);
    }

}
