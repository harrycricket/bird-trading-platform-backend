package com.gangoffive.birdtradingplatform.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddressDto {
    private long id;
    private String address;
}
