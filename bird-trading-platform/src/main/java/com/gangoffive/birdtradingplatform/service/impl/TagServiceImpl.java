package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.SuccessResponse;
import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.TagService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    @Override
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    @Override
    public ResponseEntity<?> addNewTag(String name) {
        Tag tag = new Tag();
        tag.setName(name);
        tagRepository.save(tag);
        SuccessResponse successResponse = SuccessResponse.builder()
                .successCode(String.valueOf(HttpStatus.OK.value()))
                .successMessage("Id: " + tag.getId())
                .build();
        return new ResponseEntity<>(successResponse, HttpStatus.OK);
    }
}
