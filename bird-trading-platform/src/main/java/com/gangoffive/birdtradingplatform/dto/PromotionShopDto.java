package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PromotionShopDto {
    private long id;
    private String name;
    private String description;
    private int discountRate;
    private long startDate;
    private long endDate;
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PromotionShopDto other = (PromotionShopDto) o;
        return id == other.id; // Compare based on the id field (or any other field that defines equality)
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Generate hash code based on the id field (or any other field that defines equality)
    }
}
