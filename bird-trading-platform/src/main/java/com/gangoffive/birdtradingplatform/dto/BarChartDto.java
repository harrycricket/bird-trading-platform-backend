package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BarChartDto {
    private String date;
    private double birds;
    private String colorBirds;
    private double accessories;
    private String colorAccessories;
    private double foods;
    private String colorFoods;
}
