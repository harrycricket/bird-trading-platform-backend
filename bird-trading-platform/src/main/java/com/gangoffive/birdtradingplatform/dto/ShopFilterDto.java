package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ShopFilterDto {
    private Long idShop;
    private int category ;
    private List<Long> listTypeId;
    private String name;
    private String sortPrice;
    private double star ;
    private double highestPrice;
    private double lowestPrice;
    private int pageNumber;
}
