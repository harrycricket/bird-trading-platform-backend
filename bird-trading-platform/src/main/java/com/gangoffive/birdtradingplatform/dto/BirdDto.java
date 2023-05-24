package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.enums.Gender;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
public class BirdDto extends ProductDto{
	protected Integer age;

    protected Gender gender;

    protected String color;

    private TypeBird typeBird;

    @Override
    public String
    toString() {
        return "BirdDto{" +
                "age=" + age +
                ", gender=" + gender +
                ", color='" + color + '\'' +
                ", typeBird=" + typeBird +
                ", quantity=" + quantity +
                ", imgUrl='" + imgUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", shopId=" + shopId +
                '}';
    }
}
