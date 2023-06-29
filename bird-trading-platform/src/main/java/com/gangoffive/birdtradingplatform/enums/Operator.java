package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Operator {
    EQUAL("="),
    GREATER_THAN_OR_EQUAL(">="),
    CONTAIN("Contain"),
    FROM_TO("Range");
    private String operator;
}
