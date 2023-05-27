package com.gangoffive.birdtradingplatform.service;
import com.gangoffive.birdtradingplatform.dto.ProductDto;
import com.gangoffive.birdtradingplatform.entity.OrderDetail;
import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;

import java.util.List;

public interface ProductService {
    List<ProductDto> retrieveAllProduct();
    List<ProductDto> retrieveProductByPagenumber(int pageNumber);
    double CalculateSaleOff(List<PromotionShop>  listPromotion, double price);
    double CalculationRating(List<OrderDetail> orderDetails);
    List<ProductDto> findProductByName(String name);
    List<ProductDto> listModelToDto(List<Product> products);
    List<ProductDto> retrieveTopProduct();
}
