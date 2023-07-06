package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseDto <T> {
    private TokenDto token;
    private T userInfo;
    private int role;
}
