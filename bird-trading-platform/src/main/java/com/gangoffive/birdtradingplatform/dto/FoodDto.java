package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto extends ProductDto{

    protected double weight;

    private TypeFood typeFood;


}
