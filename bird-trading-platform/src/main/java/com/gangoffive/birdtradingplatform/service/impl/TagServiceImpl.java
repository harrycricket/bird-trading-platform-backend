package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.TagDto;
import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.repository.TagRepository;
import com.gangoffive.birdtradingplatform.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        Optional<Tag> tag = tagRepository.findByName(name);
        if (tag.isPresent()) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .errorCode(String.valueOf(HttpStatus.CONFLICT.value()))
                    .errorMessage("Tag name already exist.")
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
        }
        Tag newTag = new Tag();
        newTag.setName(name);
        Tag save = tagRepository.save(newTag);
        TagDto tagDto = TagDto.builder()
                .id(save.getId())
                .name(save.getName())
                .build();
        return new ResponseEntity<>(tagDto, HttpStatus.OK);
    }
}
