package com.gangoffive.birdtradingplatform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountUpdateDto {
    private String email;
    private String fullName;
    private String phoneNumber;
    private String street;
    private String ward;
    private String district;
    private String city;
}
