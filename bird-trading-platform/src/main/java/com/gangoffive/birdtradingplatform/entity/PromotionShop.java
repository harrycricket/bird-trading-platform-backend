/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.gangoffive.birdtradingplatform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;

/**
 *
 * @author Admins
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tblPromotion_Shop")
@Where(clause = "start_date <= CURRENT_DATE and end_date >= CURRENT_DATE")
public class PromotionShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "promotion_s_id")
    private Long id;

    @Column
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "discount_rate")
    private int discountRate;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;


    @ManyToMany(mappedBy = "promotionShops")
    private List<Product> products;

    @ManyToMany(mappedBy = "promotionShops")
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(name = "shop_id",
        foreignKey = @ForeignKey(name = "FK_PROMOTION_SHOP_SHOP_OWNER"))
    private ShopOwner shopOwner;

    public void addProduct(Product product) {
        this.products.add(product);
    }
    
     public void addOrder(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
    }

    public int getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(int discountRate) {
        this.discountRate = discountRate;
    }
}
