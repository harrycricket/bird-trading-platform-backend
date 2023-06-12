package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BumpChartDto {
    String type;
    List<DataBumpChartDto> dataBumpCharts;
}
