package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UserAccountDto {
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private String address;
    private AccountStatus status;
    private Long createdDate;
}
