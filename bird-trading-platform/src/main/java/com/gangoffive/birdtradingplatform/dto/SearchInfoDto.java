package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SearchInfoDto {
    private Long id;
    private String field;
    private String value;
    private String operator;
}
