package com.gangoffive.birdtradingplatform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatusShopChangeDto {
    private List<Long> ids;
    private int status;
}
