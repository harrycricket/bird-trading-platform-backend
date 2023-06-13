package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class LineChartDto {
    String id;
    List<DataLineChartDto> data;
}
