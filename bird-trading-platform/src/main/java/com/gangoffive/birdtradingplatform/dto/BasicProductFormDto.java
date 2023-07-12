package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BasicProductFormDto {
    private Long id;
    private String name;
    private Long category;
}
