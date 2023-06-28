package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProductShopOwnerSearchInfoDto {
    private String field;
    private String value;
    private String operator;
}
