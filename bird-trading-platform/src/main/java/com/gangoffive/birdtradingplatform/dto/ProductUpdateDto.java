package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.Gender;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProductUpdateDto {
    private BasicProductFormDto basicForm;
    private FeatureDto feature;
    private DetailProductFormDto detailsForm;
    private SaleProductFormDto salesForm;
    private List<String> listImages;
}
