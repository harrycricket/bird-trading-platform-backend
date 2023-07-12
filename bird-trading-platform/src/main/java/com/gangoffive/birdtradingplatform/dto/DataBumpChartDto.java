package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DataBumpChartDto {
    private String id;
    private List<BumpChartDto> data;
}
