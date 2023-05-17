package com.gangoffive.birdtradingplatform.entity;

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
    @SequenceGenerator(
            name = "type_accessory_id_seq",
            sequenceName = "type_accessory_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "type_accessory_id_seq"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "typeAccessory")
    private List<Accessory> accessories = new ArrayList<>();

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
}
