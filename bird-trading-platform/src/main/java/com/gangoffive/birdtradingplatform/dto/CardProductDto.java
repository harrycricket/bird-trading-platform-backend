package com.gangoffive.birdtradingplatform.dto;

import lombok.Data;

@Data
public class CardProductDto {
        private long id;
        private String name;
        private String imgUrl;
        private double price;
        private ShopOwnerDto shopOwner;
        private double discountRate;
        private double discountedPrice;
        private double star;
}
