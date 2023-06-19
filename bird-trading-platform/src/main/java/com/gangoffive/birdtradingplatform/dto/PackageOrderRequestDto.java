package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PackageOrderRequestDto {
    private UserOrderDto userOrderDto;
    private TransactionDto transactionDto;
    private Map<Long, Integer> productOrder;
}
