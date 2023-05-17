package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tblType_Bird")
@AllArgsConstructor
@NoArgsConstructor
public class TypeBird {
    @Id
    @SequenceGenerator(
            name = "type_bird_id_seq",
            sequenceName = "type_bird_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "type_bird_id_seq"
    )
    private Long id;
    @Column(nullable = false)

    private String name;

    @OneToMany(mappedBy = "typeBird")
    private List<Bird> birds = new ArrayList<>();
}
