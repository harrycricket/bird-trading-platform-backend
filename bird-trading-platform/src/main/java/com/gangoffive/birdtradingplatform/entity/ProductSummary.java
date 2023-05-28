package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "tblProduct_Summary")
@Data
public class ProductSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_summary_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id",
        foreignKey = @ForeignKey(name = "FK_PRODUCT_SUMMARY_PRODUCT")
    )
    private Product product;

    @Column(name = "star")
    private double star;

    @Column(name = "review_total")
    private int reviewTotal;

    @Column(name = "total_quantity_order")
    private double totalQuantityOrder;
}
