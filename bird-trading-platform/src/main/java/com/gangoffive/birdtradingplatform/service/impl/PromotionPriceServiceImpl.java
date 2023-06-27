package com.gangoffive.birdtradingplatform.service.impl;

import com.gangoffive.birdtradingplatform.entity.Product;
import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import com.gangoffive.birdtradingplatform.service.PromotionPriceService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class PromotionPriceServiceImpl implements PromotionPriceService {
    @Override
    public double CalculateSaleOff(List<PromotionShop> listPromotion, double price) {
        if (listPromotion != null && listPromotion.size() != 0) {
            List<Integer> saleOff = listPromotion.stream().map(s -> (Integer) s.getDiscountRate()).collect(Collectors.toList());
            double priceDiscount = price;
            for (double sale : saleOff) {
                priceDiscount = priceDiscount - priceDiscount * sale / 100;
            }
            double percentDiscount = Math.round(((price - priceDiscount) / price) * 100.0) / 100.0;

            return percentDiscount;
        }
        return 0.0;
    }

    @Override
    public double CalculateDiscountedPrice(double price, double saleOff) {
        return Math.round((price - (price * saleOff)) * 100.0) / 100.0;
    }

    @Override
    public double getDiscountedPrice(Product product) {
        double saleOf = this.CalculateSaleOff(product.getPromotionShops(), product.getPrice());
        double result = this.CalculateDiscountedPrice(product.getPrice(), saleOf);
        return result;
    }
}
