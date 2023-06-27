package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Where;

@Entity(name = "tblProduct_Summary")
@Data
@Where(clause = "is_deleted = false")
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

    @Column(name = "category")
    private String category;

    @Column(name = "discounted_price")
    private double discountedPrice;

    @Column(name = "total_quantity_order")
    private double totalQuantityOrder;

    @Column(name = "discounted_price")
    private double discountedPrice;

    @Column(name = "is_deleted")
    private boolean deleted;
}
