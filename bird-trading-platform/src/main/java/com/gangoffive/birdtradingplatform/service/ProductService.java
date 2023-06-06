package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.BirdDto;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import com.google.gson.JsonObject;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ProductService {
    List<ProductDto> retrieveAllProduct();
    ResponseEntity<?> retrieveProductByPagenumber(int pageNumber);
    double CalculationRating(List<OrderDetail> orderDetails);
    double CalculateSaleOff(List<PromotionShop>  listPromotion, double price);
    List<ProductDto> findProductByName(String name);
    List<ProductDto> retrieveTopProduct();
    List<ProductDto> listModelToDto(List<Product> products);
    JsonObject retrieveProductById(Long id);

    double CalculateDiscountedPrice(double price, double saleOff);

    ProductDto ProductToDto(Product product);
}
