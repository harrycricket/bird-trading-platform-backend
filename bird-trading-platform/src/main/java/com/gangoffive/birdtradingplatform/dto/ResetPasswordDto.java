package com.gangoffive.birdtradingplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordDto {
    private String email;
    private Long verifyId;
    private Integer code;
    private String newPassword;
}
