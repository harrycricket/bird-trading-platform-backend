package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum Operator {
    EQUAL("Equal"),
    GREATER_THAN_OR_EQUAL("GreaterThanOrEqual"),
    LIKE("Like");
    private String operator;
}
