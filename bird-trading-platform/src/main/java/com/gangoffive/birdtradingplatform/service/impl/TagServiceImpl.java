package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.api.response.ErrorResponse;
import com.gangoffive.birdtradingplatform.dto.ProductTagDto;
import com.gangoffive.birdtradingplatform.dto.TagDto;
import com.gangoffive.birdtradingplatform.dto.TagShopDto;
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

import java.util.*;
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
                if (bird.getTags() != null) {
                    tags.addAll(bird.getTags());
                }
            }));

            foods.ifPresent(foodList -> foodList.forEach(food -> {
                if (food.getTags() != null) {
                    tags.addAll(food.getTags());
                }
            }));

            accessories.ifPresent(accessoryList -> accessoryList.forEach(accessory -> {
                if (accessory.getTags() != null) {
                    tags.addAll(accessory.getTags());
                }
            }));

            if (tags.size() == 0) {
                ResponseUtils.getErrorResponseNotFound("Not found tag in this shop.");
            }

            // Create a map to store the count of each tag
            Map<Tag, Integer> tagCountMap = new HashMap<>();
            for (Tag tag : tags) {
                tagCountMap.put(tag, tagCountMap.getOrDefault(tag, 0) + 1);
            }

// Filter out the tags that have at least two products
            List<Tag> tagsWithTwoProducts = tagCountMap.entrySet().stream()
                    .filter(entry -> entry.getValue() >= 2)
                    .map(Map.Entry::getKey)
                    .distinct()
                    .toList();
            List<TagShopDto> tagShopList = new ArrayList<>();
            tagsWithTwoProducts.forEach(tag -> {
                List<ProductTagDto> productTagList = new ArrayList<>();
                final int[] count = {0};
                if (count[0] < 2) {
                    Optional<List<Bird>> birdList = birdRepository.findByTagsInAndShopOwner_IdAndStatus(
                            Collections.singletonList(tag), shopId, ProductStatus.ACTIVE
                    );
                    if (birdList.isPresent()) {
                        birdList.get().forEach(bird -> {
                            if (count[0] >= 2) {
                                return;
                            }
                            productTagList.add(ProductTagDto.builder()
                                    .id(bird.getId())
                                    .name(bird.getName())
                                    .urlImg(Arrays.stream(bird.getImgUrl().split(",")).findFirst().get().toString())
                                    .build());
                            count[0]++;
                        });
                    }
                }

                if (count[0] < 2) {
                    Optional<List<Food>> foodList = foodRepository.findByTagsInAndShopOwner_IdAndStatus(
                            Collections.singletonList(tag), shopId, ProductStatus.ACTIVE
                    );
                    if (foodList.isPresent()) {
                        foodList.get().forEach(food -> {
                            if (count[0] >= 2) {
                                return;
                            }
                            productTagList.add(ProductTagDto.builder()
                                    .id(food.getId())
                                    .name(food.getName())
                                    .urlImg(Arrays.stream(food.getImgUrl().split(",")).findFirst().get().toString())
                                    .build());
                            count[0]++;
                        });
                    }
                }

                if (count[0] < 2) {
                    Optional<List<Accessory>> accessoryList = accessoryRepository.findByTagsInAndShopOwner_IdAndStatus(
                            Collections.singletonList(tag), shopId, ProductStatus.ACTIVE
                    );
                    if (accessoryList.isPresent()) {
                        accessoryList.get().forEach(accessory -> {
                            if (count[0] >= 2) {
                                return;
                            }
                            productTagList.add(ProductTagDto.builder()
                                    .id(accessory.getId())
                                    .name(accessory.getName())
                                    .urlImg(Arrays.stream(accessory.getImgUrl().split(",")).findFirst().get().toString())
                                    .build());
                            count[0]++;
                        });
                    }
                }

                TagShopDto tagShop = TagShopDto.builder()
                        .tag(tag)
                        .productTagList(productTagList)
                        .build();
                tagShopList.add(tagShop);
            });
            if (tagsWithTwoProducts.isEmpty()) {
                return ResponseUtils.getErrorResponseNotFound("Not found any tag with at least two products in this shop.");
            } else {
                return ResponseEntity.ok(tagShopList);
            }
//            List<Tag> distinctTags = tags.stream()
//                    .collect(Collectors.toMap(Tag::getId, Function.identity(), (tag1, tag2) -> tag1))
//                    .values()
//                    .stream().toList();
        } else {
            return ResponseUtils.getErrorResponseBadRequest("Not found this shop id");
        }
    }
}
