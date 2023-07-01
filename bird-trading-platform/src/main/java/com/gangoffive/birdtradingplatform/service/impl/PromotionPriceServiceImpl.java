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
            double percentDiscount = (price - priceDiscount) / price;

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

    @Override
    public double calculatePercentDiscountedOfProductByPromotions(
            List<PromotionShop> promotionShops, double discountedPrice
    ) {
        if (promotionShops.isEmpty()) {
            return 0;
        }
        double originPrice = 0;
        for (int i = 0; i < promotionShops.size(); i++) {
            if (i == 0) {
                originPrice = discountedPrice + discountedPrice * (promotionShops.get(i).getDiscountRate() * 1.0 / 100);
                log.info("originPrice 0 {}", originPrice);
            } else {
                originPrice = originPrice + originPrice * (promotionShops.get(i).getDiscountRate() * 1.0 / 100);
                log.info("originPrice > 0 {}", originPrice);
            }
        }
        return Math.round(((originPrice - discountedPrice) / originPrice) * 100.0) / 100.0;
    }
}
