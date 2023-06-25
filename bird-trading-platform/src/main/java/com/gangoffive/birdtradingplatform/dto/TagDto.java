package com.gangoffive.birdtradingplatform.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TagDto {
    private Long id;
    private String name;
}
