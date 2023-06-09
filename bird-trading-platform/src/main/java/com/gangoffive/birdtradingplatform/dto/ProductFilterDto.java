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
    private List<Long> listtypeId;
    private int catelory ;
    private double star ;
    private String arrange;
    private int highestprice;
    private int lowestprice;

}
