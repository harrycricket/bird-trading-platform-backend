package com.gangoffive.birdtradingplatform.dto;

import lombok.*;
import org.springframework.stereotype.Controller;

import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ProductFilterDto {
    private int category ;
    private List<Long> listTypeId;
    private String name;
    private String sortPrice;
    private double star ;
    private double highestPrice;
    private double lowestPrice;
    private int pageNumber;
}
