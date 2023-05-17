package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tblAccessory")
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Accessory extends Product {
    @Column(nullable = false)
    protected String origin;

    @ManyToOne
    private TypeAccessory typeAccessory;

    @ManyToMany(mappedBy = "accessories")
    private List<Tag> tags = new ArrayList<>();

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public TypeAccessory getTypeAccessory() {
        return typeAccessory;
    }

    public void setTypeAccessory(TypeAccessory typeAccessory) {
        this.typeAccessory = typeAccessory;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void addTags(Tag tag) {
        this.tags.add(tag);
    }
}
