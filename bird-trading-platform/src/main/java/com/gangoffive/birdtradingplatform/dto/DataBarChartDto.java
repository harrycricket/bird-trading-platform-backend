package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DataBarChartDto {
    private double total;
    private double percent;
    private List<BarChartDto> barChartDtoList;
}
