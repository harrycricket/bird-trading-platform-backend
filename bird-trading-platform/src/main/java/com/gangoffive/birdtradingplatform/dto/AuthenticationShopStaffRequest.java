package com.gangoffive.birdtradingplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationShopStaffRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private Long shopId;
}
