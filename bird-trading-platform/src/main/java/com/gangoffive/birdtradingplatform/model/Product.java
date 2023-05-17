package com.gangoffive.birdtradingplatform.model;

import java.util.Date;

public abstract class Product {
    Long id;
    String name;
    double price;
    String description;
    Date createdDate;
    Date lastUpDated;
    Integer quantity;
    String imgUrl;
    String videoUrl;
    
}
