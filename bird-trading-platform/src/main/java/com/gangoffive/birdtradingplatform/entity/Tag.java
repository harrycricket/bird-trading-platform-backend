package com.gangoffive.birdtradingplatform.entity;

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
    @SequenceGenerator(
            name = "tag_id_seq",
            sequenceName = "tag_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "tag_id_seq"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "tblTag_Bird", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "bird_id")
    )
    private List<Bird> birds = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tblTag_Accessory", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "accessory_id")
    )
    private List<Accessory> accessories = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tblTag_Food", joinColumns = @JoinColumn(name = "tag_id"), inverseJoinColumns = @JoinColumn(name = "food_id")
    )
    private List<Food> foods = new ArrayList<>();
}
