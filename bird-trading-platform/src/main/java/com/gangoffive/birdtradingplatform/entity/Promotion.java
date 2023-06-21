/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.PromotionType;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 *
 * @author Admins
 */
@Entity
@Table(name = "tblPromotion")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_id")
    private Long id;

    @Column
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private double discount;

    @Column
    @Enumerated(value = EnumType.STRING)
    private PromotionType type;

    @Column(name = "minimum_order_value")
    private double minimumOrderValue;

    @Column(name = "usage_limit")
    private int usageLimit;

    @Column
    private Date startDate;

    @Column
    private Date endDate;

    @ManyToMany(mappedBy = "promotions")
    private List<PackageOrder> packageOrders;

    public void addOrder(PackageOrder packageOrder){
        this.packageOrders.add(packageOrder);
    }
}
