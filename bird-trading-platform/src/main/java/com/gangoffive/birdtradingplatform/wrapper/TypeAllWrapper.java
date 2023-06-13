package com.gangoffive.birdtradingplatform.wrapper;

import com.gangoffive.birdtradingplatform.entity.TypeAccessory;
import com.gangoffive.birdtradingplatform.entity.TypeBird;
import com.gangoffive.birdtradingplatform.entity.TypeFood;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TypeAllWrapper {
    List<TypeBird> typeBirds;
    List<TypeFood> typeFoods;
    List<TypeAccessory> typeAccessories;
}
