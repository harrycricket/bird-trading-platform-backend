package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class DataBumpChartDto {
    private String id;
    private List<BumpChartDto> bumpChartDtoList;
}
