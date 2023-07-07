package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfoDto {
    private long id;
    private String fullName;
    private String address;
    private String phone;
}
