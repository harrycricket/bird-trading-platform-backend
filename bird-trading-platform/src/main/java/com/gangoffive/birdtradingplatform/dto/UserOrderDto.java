package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserOrderDto {
    private String email;
    private String name;
    private String phoneNumber;
    private String address;
}
