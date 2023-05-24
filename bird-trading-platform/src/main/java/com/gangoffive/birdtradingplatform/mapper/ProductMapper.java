package com.gangoffive.birdtradingplatform.mapper;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.entity.Product;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;

//@Mapper(componentModel = "spring")
public interface ProductMapper {
//    @Mapping(source = "product.shopOwner.id" ,target = "shopId")
    ProductDto toDto (Product product);

    Product toModel(ProductDto productDto);
}
