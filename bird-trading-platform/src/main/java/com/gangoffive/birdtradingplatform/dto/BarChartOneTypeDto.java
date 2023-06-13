package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BarChartOneTypeDto {
    private String date;
    private double value;
    private String color;
}
