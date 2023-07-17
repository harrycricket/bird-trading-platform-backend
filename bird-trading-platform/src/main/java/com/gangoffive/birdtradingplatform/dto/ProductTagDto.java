package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProductTagDto {
    private Long id;
    private String name;
    private String urlImg;
}
