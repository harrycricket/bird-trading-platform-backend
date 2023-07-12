package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class AccountReviewDto {
    private Long id;
    private String fullName;
    private String phone;
    private String imgUrl;
    private String address;
}
