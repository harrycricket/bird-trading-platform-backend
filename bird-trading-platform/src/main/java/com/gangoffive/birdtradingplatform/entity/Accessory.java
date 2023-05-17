package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblAccessory")
@Getter
@Setter
public class Accessory extends Product {
    @Column(nullable = false)
    protected String origin;

    @ManyToOne
    private TypeAccessory typeAccessory;

    @ManyToMany(mappedBy = "accessories")
    private List<Tag> tags = new ArrayList<>();

    public Accessory() {
    }

    public Accessory(Long id, String name, double price, String description, Date createdDate,
                     Date lastUpDated, Integer quantity, String imgUrl, String videoUrl, String origin) {
        super(id, name, price, description, createdDate, lastUpDated, quantity, imgUrl, videoUrl);
        this.origin = origin;
    }


}
