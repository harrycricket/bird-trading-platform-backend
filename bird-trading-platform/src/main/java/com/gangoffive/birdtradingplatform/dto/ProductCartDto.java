package com.gangoffive.birdtradingplatform.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class ProductCartDto {
    protected long id;
    protected String name;
    protected String imgUrl;
    protected double price;
    protected double discountedPrice;
    protected double discountRate;
    protected int quantity;
    protected int categoryId;
    private ShopOwnerDto shopOwner;
    private double star;
    private List<TagDto> tags;
    private TypeDto type;
}
