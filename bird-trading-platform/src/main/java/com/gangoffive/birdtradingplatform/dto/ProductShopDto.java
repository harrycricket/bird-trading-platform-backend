package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.PromotionShop;
import com.gangoffive.birdtradingplatform.entity.Tag;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductShopDto <T> {
    private long id;
    private String name;
    private int category;
    private double price;
//    private List<PromotionShopDto> listDiscount;
    private int quantity;
    private T  type ;
    private String status;
    private double totalOrders;
    private double star;
    private int totalReviews;
    private long createDate;
    private long lastUpdate;
}
