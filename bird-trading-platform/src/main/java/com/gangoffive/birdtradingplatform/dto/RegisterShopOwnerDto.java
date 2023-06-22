package com.gangoffive.birdtradingplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterShopOwnerDto {
    private String shopName;
    private String phoneShop;
    private String shopAddress;
    private String description;
}
