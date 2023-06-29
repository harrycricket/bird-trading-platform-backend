package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ShopOwnerSearchInfoDto {
    private Long id;
    private String field;
    private String value;
    private String operator;
}
