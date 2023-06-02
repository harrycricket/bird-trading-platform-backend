package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.Tag;
import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AccessoryDto extends ProductDto{
    private String origin;

    private TypeAccessory type;

    private List<Tag> tags;

}
