package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.TagDto;
import com.gangoffive.birdtradingplatform.entity.*;
import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import com.gangoffive.birdtradingplatform.enums.ShopOwnerStatus;
import com.gangoffive.birdtradingplatform.repository.*;
import com.gangoffive.birdtradingplatform.service.TagService;
import com.gangoffive.birdtradingplatform.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;
    private final ShopOwnerRepository shopOwnerRepository;
    private final BirdRepository birdRepository;
    private final AccessoryRepository accessoryRepository;
    private final FoodRepository foodRepository;

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

    @Override
    public ResponseEntity<?> getAllTagByShopOwnerId(Long shopId) {
        Optional<ShopOwner> shopOwner = shopOwnerRepository.findByIdAndStatus(shopId, ShopOwnerStatus.ACTIVE);
        if (shopOwner.isPresent()) {
            List<Tag> tags = new ArrayList<>();
            Optional<List<Bird>> birds = birdRepository.findAllByShopOwnerAndStatus(shopOwner.get(), ProductStatus.ACTIVE);
            Optional<List<Food>> foods = foodRepository.findAllByShopOwnerAndStatus(shopOwner.get(), ProductStatus.ACTIVE);
            Optional<List<Accessory>> accessories = accessoryRepository.findAllByShopOwnerAndStatus(shopOwner.get(), ProductStatus.ACTIVE);
            birds.ifPresent(birdList -> birdList.forEach(bird -> {
                tags.addAll(bird.getTags());
            }));

            foods.ifPresent(foodList -> foodList.forEach(food -> {
                tags.addAll(food.getTags());
            }));

            accessories.ifPresent(accessoryList -> accessoryList.forEach(accessory -> {
                tags.addAll(accessory.getTags());
            }));

            if (tags.size() == 0) {
                ResponseUtils.getErrorResponseNotFound("Not found tag in this shop.");
            }

            List<Tag> distinctTags = tags.stream()
                    .collect(Collectors.toMap(Tag::getId, Function.identity(), (tag1, tag2) -> tag1))
                    .values()
                    .stream().toList();
            return ResponseEntity.ok(distinctTags);
        } else {
            return ResponseUtils.getErrorResponseBadRequest("Not found this shop id");
        }
    }
}
