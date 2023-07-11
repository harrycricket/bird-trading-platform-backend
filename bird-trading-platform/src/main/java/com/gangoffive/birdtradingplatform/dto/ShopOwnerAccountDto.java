package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.ShopOwnerStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ShopOwnerAccountDto {
    private Long id;
    private String email;
    private String shopName;
    private String shopPhone;
    private String address;
    private ShopOwnerStatus status;
    private Long createdDate;
}
