package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderDetailDto {
    private Long productId;
    private String name;
    private int quantity;
    private double price;
}
