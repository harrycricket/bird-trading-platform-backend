package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.Gender;
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
    private String description;
    private int quantity;
    private List<Long> promotionShopId;
    private Long typeId;
    private List<String> nameTag;
    private Long categoryId;
    private int age;
    private Gender gender;
    private String color;
    private double weight;
    private String origin;
}
