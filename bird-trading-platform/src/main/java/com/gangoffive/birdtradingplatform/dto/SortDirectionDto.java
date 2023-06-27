package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.enums.SortDirection;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SortDirectionDto {
    private String sortColumn;
    private SortDirection sortDirection;
}
