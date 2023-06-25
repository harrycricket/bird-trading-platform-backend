package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.Gender;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ProductShopOwnerDto {
    private String name;
    private double price;
    private int quantity;
    private String description;
    private List<Long> promotionShopId;
    private Long typeId;
    private List<Long> tagId;
    private Long categoryId;
    private FeatureDto feature;
}
