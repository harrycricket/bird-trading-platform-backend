package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.ShopOwner;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateAccountSaffDto {
    private String userName;
    private String password;
    private String confirmPassword;
}
