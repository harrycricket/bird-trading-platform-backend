package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class Product {

    @Id
    @SequenceGenerator(
            name = "product_id_seq",
            sequenceName = "product_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "product_id_seq"
    )
    protected Long id;


    @Column(nullable = false)
    protected String name;

    @Column(nullable = false)
    protected double price;

    @Column(nullable = false)
    protected String description;

    @Column(name = "created_date")
    @CreationTimestamp
    protected Date createdDate;

    @Column(name = "last_updated")
    @UpdateTimestamp
    protected Date lastUpDated;

    @Column(nullable = false)
    protected Integer quantity;

    @Column(name = "img_url", nullable = false)
    protected String imgUrl;

    @Column(name = "video_url")
    protected String videoUrl;
}
