package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DataBumpChartDto {
    LocalDate dateOfPrice;
    double price;
}
