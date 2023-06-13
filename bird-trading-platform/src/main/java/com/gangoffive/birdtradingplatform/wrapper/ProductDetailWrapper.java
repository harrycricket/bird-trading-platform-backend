package com.gangoffive.birdtradingplatform.wrapper;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ProductDetailWrapper {
    private ProductDto product;
    private List<String> listImages;
    private int numberSold;
    private int numberReview;

}
