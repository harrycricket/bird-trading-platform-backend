package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReviewShopOwnerDto {
    private Long id;
    private Long orderDetailId;
    private String customerName;
    private String productName;
    private int rating;
    private Long reviewDate;
}
