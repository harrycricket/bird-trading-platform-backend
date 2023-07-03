package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.Gender;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import com.gangoffive.birdtradingplatform.enums.Gender;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "tblBird")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@SQLDelete(sql = "update tbl_Bird set is_deleted = true where product_id = ?")
public class Bird extends Product {
    protected int age;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    protected Gender gender;

    @Column(nullable = false)
    protected String color;

    @ManyToOne
    @JoinColumn(
            name = "type_id",
            foreignKey = @ForeignKey(name = "FK_BIRD_TYPE_BIRD")
    )
    private TypeBird typeBird;

    @ManyToMany(mappedBy = "birds")
    private List<Tag> tags = new ArrayList<>();

    public void addTags(Tag tag) {
        this.tags.add(tag);
    }
}
