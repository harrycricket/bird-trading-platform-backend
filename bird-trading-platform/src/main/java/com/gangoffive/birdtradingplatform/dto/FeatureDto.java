package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.Gender;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class FeatureDto {
    private Integer age;
    private Gender gender;
    private String color;
    private double weight;
    private String origin;
}
