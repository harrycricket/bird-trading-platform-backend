package com.gangoffive.birdtradingplatform.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AccountUpdateDto {
    String email;
    String fullName;
    String phoneNumber;
    String street;
    String ward;
    String district;
    String city;
}
