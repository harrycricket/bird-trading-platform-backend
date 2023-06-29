package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderStatusDto {
    private int id;
    private OrderStatus status;
}
