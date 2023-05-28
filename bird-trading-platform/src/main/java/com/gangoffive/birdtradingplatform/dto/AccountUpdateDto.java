package com.gangoffive.birdtradingplatform.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountUpdateDto {
    String email;
    String fullName;
    String phoneNumber;
    String street;
    String ward;
    String district;
    String city;
}
