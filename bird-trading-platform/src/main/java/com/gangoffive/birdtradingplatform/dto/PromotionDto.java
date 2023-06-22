package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.PromotionType;
import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PromotionDto {
    private Long id;
    private String name;
    private String description;
    private double discount;
    private double minimumOrderValue;
    private Long limit;
    private Long used;
    private PromotionType type;
    private Long endDate;
}
