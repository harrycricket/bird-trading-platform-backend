package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.SortPriceDirection;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShopFilterDto {
    private Long shopId;
    private int categoryId;
    private List<Long> listTypeId;
    private String name;
    private SortPriceDirection sortPrice;
    private double star ;
    private double highestPrice;
    private double lowestPrice;
    private int pageNumber;
}
