package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.ReviewRating;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderDetailShopOwnerDto {
//    id orderId product price quantity review createDate promotionRate
    private Long orderId;
    private Long id;
    private Long productId;
    private String name;
    private double price;
    private int quantity;
    private double promotionRate;
    private Long createdDate;
    private int reviewRating;
}
