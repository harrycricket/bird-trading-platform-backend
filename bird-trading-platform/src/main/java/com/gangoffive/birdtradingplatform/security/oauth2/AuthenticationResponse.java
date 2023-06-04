package com.gangoffive.birdtradingplatform.security.oauth2;

import com.gangoffive.birdtradingplatform.dto.AddressDto;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private String accessToken;
    private String refreshToken;
    private String email;
    private UserRole role;
    private String fullName;
    private String phoneNumber;
    private String imgUrl;
    private AddressDto address;
}
