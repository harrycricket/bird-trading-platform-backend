package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PackageOrderRequestDto {
    private UserOrderDto userInfo;
    private CartDto cartInfo;
}
