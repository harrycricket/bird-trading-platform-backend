package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblFood")
@Getter
@Setter
public class Food extends Product {
    @Column(nullable = false)
    protected double weight;

    @ManyToOne
    private TypeFood typeFood;

    @ManyToMany(mappedBy = "foods")
    private List<Tag> tags = new ArrayList<>();

    public Food() {
    }
    public Food(Long id, String name, double price, String description, Date createdDate,
                Date lastUpDated, Integer quantity, String imgUrl, String videoUrl, double weight) {
        super(id, name, price, description, createdDate, lastUpDated, quantity, imgUrl, videoUrl);
        this.weight = weight;
    }


}
