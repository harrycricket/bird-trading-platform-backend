package com.gangoffive.birdtradingplatform.dto;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.gangoffive.birdtradingplatform.entity.ShopOwner;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ProductDto {
	private Long id;

    protected String name;

    protected Integer quantity;

    protected String imgUrl;

    protected String videoUrl;

    protected double price;

    protected ShopOwnerDto shopOwner;

    protected double discountRate;

    protected int star;





}
