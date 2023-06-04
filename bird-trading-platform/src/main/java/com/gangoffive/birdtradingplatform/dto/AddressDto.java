package com.gangoffive.birdtradingplatform.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private String street;

    private String ward;

    private String district;

    private String city;
}
