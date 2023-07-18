package com.gangoffive.birdtradingplatform.dto;

import com.gangoffive.birdtradingplatform.entity.Tag;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TagShopDto {
    private Tag tag;
    private List<ProductTagDto> productTagList;
}
