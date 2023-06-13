package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class PieChartDto {
    private String id;
    private String label;
    private double value;
    private String color;
}
