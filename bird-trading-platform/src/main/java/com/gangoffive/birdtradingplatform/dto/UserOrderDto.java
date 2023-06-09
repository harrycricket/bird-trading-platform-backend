package com.gangoffive.birdtradingplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserOrderDto {
    private String email;
    private String name;
    private String phoneNumber;
    private String street;
    private String ward;
    private String district;
    private String city;
}
