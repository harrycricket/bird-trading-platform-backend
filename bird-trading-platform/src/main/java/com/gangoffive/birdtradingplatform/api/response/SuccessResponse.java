package com.gangoffive.birdtradingplatform.api.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class SuccessResponse {
    private String successCode;
    private String successMessage;

    public SuccessResponse(String successCode, String successMessage) {
        this.successCode = successCode;
        this.successMessage = successMessage;
    }
}
