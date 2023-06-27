package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.SortDirection;
import com.gangoffive.birdtradingplatform.enums.SortPriceDirection;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductShopOwnerFilterDto {
    private int category;
    private Long id;
    private String productName;
    private String typeName;
    private double lowestPrice;
    private String status;
    private String sortColumn;
    private SortDirection sortDirection;
    private int pageNumber;
}
