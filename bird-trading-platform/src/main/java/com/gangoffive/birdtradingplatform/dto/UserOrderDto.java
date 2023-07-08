package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserOrderDto {
    private String fullName;
    private String phoneNumber;
    private String address;
}
