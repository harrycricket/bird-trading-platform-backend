package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblBird")
@Getter
@Setter
public class Bird extends Product {
    protected Integer age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Gender gender;

    @Column(nullable = false)
    protected String color;

    @ManyToOne
    private TypeBird typeBird;

    @ManyToMany(mappedBy = "birds")
    private List<Tag> tags = new ArrayList<>();

    public Bird() {
    }

    public Bird(Long id, String name, double price, String description, Date createdDate, Date lastUpDated,
                Integer quantity, String imgUrl, String videoUrl, Integer age, Gender gender, String color) {
        super(id, name, price, description, createdDate, lastUpDated, quantity, imgUrl, videoUrl);
        this.age = age;
        this.gender = gender;
        this.color = color;
    }


}
