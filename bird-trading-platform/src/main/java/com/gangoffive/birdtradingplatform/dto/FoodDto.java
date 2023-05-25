package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FoodDto extends ProductDto{

    protected double weight;

    private TypeFood typeFood;

    @Override
    public String toString() {
        return "FoodDto{" +
                "weight=" + weight +
                ", typeFood=" + typeFood +
                ", quantity=" + quantity +
                ", imgUrl='" + imgUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", shopId=" + shopId +
                '}';
    }
}
