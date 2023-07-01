package com.gangoffive.birdtradingplatform.dto;

import lombok.Data;

@Data
public class ShopOwnerUpdateDto {
    private long id;
    private String shopName;
    private String shopPhone;
    private String description;
    private String address;
}
