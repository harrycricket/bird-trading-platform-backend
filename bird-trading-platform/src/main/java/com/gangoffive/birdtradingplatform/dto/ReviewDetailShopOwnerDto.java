package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ReviewDetailShopOwnerDto {
    private Long id;
    private AccountReviewDto account;
    private Long orderDetailId;
    private Long productId;
    private String productName;
    private String description;
    private int rating;
    private List<String> imgUrl;
    private Long reviewDate;
}
