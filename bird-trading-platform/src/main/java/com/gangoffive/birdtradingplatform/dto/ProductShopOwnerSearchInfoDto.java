package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProductShopOwnerSearchInfoDto {
//    {field: 'name', operator: 'contains', id: 39474, value: 'asdfasdfasdfasdf'}
    private String field;
    private String value;
    private String operator;
//    private Long productId;
//    private String productName;
//    private String typeName;
//    private Double lowestPrice;
//    private Double lowestPriceOfDiscountedPrice;
//    private Integer status;
}
