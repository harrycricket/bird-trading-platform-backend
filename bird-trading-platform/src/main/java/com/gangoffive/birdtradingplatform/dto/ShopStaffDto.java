package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
public class ShopStaffDto {
    private Long id;
    private Long shopId;
    private String userName;
    private AccountStatus status;
}
