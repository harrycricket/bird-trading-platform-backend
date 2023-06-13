package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class FoodDto extends ProductDto{

    private double weight;

    private TypeFood type;

    private List<Tag> tags;

}
