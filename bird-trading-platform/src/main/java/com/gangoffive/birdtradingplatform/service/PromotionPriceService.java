package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;

import java.util.List;

public interface PromotionPriceService {
    double CalculateSaleOff(List<PromotionShop> listPromotion, double price);

    double CalculateDiscountedPrice(double price, double saleOff);

    double getDiscountedPrice(Product product);
}
