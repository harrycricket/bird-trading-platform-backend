package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.enums.Gender;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BirdDto extends ProductDto{
	protected Integer age;

    protected Gender gender;

    protected String color;

    private TypeBird typeBird;


}
