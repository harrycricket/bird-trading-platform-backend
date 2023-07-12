package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class DetailProductFormDto {
    private String description;
    private Long typeId;
    private List<TagDto> tags;
}
