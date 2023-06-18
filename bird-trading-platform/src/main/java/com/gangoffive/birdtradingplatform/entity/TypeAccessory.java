package com.gangoffive.birdtradingplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tblType_Accessory")
@AllArgsConstructor
@NoArgsConstructor
public class TypeAccessory {
    @Id
    @Column(name = "type_a_id")
//    @SequenceGenerator(
//            name = "type_accessory_id_seq",
//            sequenceName = "type_accessory_id_seq",
//            allocationSize = 1
//    )
//    @GeneratedValue(
//            strategy = GenerationType.IDENTITY,
//            generator = "type_accessory_id_seq"
//    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "typeAccessory")
    @JsonIgnore
    private List<Accessory> accessories = new ArrayList<>();

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

    public List<Accessory> getAccessories() {
        return accessories;
    }

    public void setAccessories(Accessory accessory) {
        this.accessories.add(accessory);
    }

    @Override
    public String toString() {
        return "TypeAccessory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
