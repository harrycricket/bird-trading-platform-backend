package com.gangoffive.birdtradingplatform.dto;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ShopInfoDto {
    private String shopName;
    private String shopPhone;
    private String description;
    private String avatarImgUrl;
    private String coverImgUrl;
    private Long createdDate;
    private AddressDto address;
}
