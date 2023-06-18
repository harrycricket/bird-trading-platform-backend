package com.gangoffive.birdtradingplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tblTag")
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    @Column(name = "tag_id")
//    @SequenceGenerator(
//            name = "tag_id_seq",
//            sequenceName = "tag_id_seq",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.SEQUENCE,
//            generator = "tag_id_seq"
//    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,
            unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "tblTag_Bird",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "bird_id"),
            foreignKey = @ForeignKey(name = "FK_TAG_BIRD")
    )
    @JsonIgnore
    private List<Bird> birds = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tblTag_Accessory",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "accessory_id"),
            foreignKey = @ForeignKey(name = "FK_TAG_ACCESSORY")
    )
    @JsonIgnore
    private List<Accessory> accessories = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tblTag_Food",
            joinColumns = @JoinColumn(name = "tag_id"),
            inverseJoinColumns = @JoinColumn(name = "food_id"),
            foreignKey = @ForeignKey(name = "FK_TAG_FOOD")
    )
    @JsonIgnore
    private List<Food> foods = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Bird> getBirds() {
        return birds;
    }

    public void addBirds(Bird bird) {
        this.birds.add(bird);
    }

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public void addAccessories(Accessory accessory) {
        this.accessories.add(accessory);
    }

    public List<Food> getFoods() {
        return foods;
    }

    public void addFoods(Food food) {
        this.foods.add(food);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
