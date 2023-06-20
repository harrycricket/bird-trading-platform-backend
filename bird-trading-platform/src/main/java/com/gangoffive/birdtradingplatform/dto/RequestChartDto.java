package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RequestChartDto {
    private String email;
    private Date date;
}
