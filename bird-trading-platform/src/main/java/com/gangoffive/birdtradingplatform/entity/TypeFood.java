package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tblType_Food")
@AllArgsConstructor
@NoArgsConstructor
public class TypeFood {
    @Id
    @SequenceGenerator(
            name = "type_food_id_seq",
            sequenceName = "type_food_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "type_food_id_seq"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "typeFood")
    private List<Food> foods = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void addFoods(Food food) {
        this.foods.add(food);
    }
}
