package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class LogOrderShopOwnerDto {
    private Long id;
    private Long orderId;
    private OrderStatus orderStatus;
    private Long timestamp;
    private Long staffId;
    private String staffUsername;
}
