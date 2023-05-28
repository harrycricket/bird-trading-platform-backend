package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;

import java.util.List;

public interface ProductService {
    List<ProductDto> retrieveAllProduct();
    List<ProductDto> retrieveProductByPagenumber(int pageNumber);
    double CalculationRating(List<OrderDetail> orderDetails);
    double CalculateSaleOff(List<PromotionShop>  listPromotion, double price);
    List<ProductDto> findProductByName(String name);
    List<ProductDto> retrieveTopProduct();
    List<ProductDto> listModelToDto(List<Product> products);
    ProductDto retrieveProductById(Long id);
}
