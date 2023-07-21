package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblFood")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@SQLDelete(sql = "update tbl_Food set is_deleted = true where product_id = ?")
public class Food extends Product {
    @Column(nullable = false)
    protected double weight;

    @ManyToOne
    @JoinColumn(
            name = "type_id",
            foreignKey = @ForeignKey(name = "FK_FOOD_TYPE_FOOD")
    )
    private TypeFood typeFood;

    @ManyToMany
    @JoinTable(
            name = "tblTag_Food",
            joinColumns = @JoinColumn(name = "food_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"),
            foreignKey = @ForeignKey(name = "FK_TAG_FOOD")
    )
    private List<Tag> tags;

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
