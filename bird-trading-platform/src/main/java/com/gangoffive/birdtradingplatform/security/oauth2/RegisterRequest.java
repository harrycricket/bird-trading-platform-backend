package com.gangoffive.birdtradingplatform.security.oauth2;

import com.gangoffive.birdtradingplatform.entity.Address;
import com.gangoffive.birdtradingplatform.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private Address address;
    private UserRole role;
}
