package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class PayoutDto {
    private String email;
    private double total;
    private String currency;
    private String emailSubject;
    private String description;
}
