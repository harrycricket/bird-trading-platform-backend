package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblFood")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Food extends Product {
    @Column(nullable = false)
    protected double weight;

    @ManyToOne
    @JoinColumn(
            name = "type_id",
            foreignKey = @ForeignKey(name = "FK_FOOD_TYPE_FOOD")
    )
    private TypeFood typeFood;

    @ManyToMany(mappedBy = "foods")
    private List<Tag> tags = new ArrayList<>();

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public TypeFood getTypeFood() {
        return typeFood;
    }

    public void setTypeFood(TypeFood typeFood) {
        this.typeFood = typeFood;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTags(Tag tag) {
        this.tags.add(tag);
    }
}
