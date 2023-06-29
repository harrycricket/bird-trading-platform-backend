package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductShopDto<T> {
    private long id;
    private String name;
    private int category;
    private double price;
    private double discountedPrice;
    //    private List<PromotionShopDto> listDiscount;
    private int quantity;
    private T type;
    private int status;
    private double totalOrders;
    private double star;
    private int totalReviews;
    private long createDate;
    private long lastUpdate;
}
