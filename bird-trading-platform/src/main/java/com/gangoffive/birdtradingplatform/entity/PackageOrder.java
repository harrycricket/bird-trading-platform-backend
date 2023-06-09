package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.PackageOrderStatus;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity(name = "tblPackage_Order")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class PackageOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "package_order_id")
    private Long id;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "discount")
    private double discount;

    @Column(name = "shipping_fee")
    private double shippingFee;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private PackageOrderStatus status;

    @Column(name = "payment_method")
    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "created_date")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "lasted_update")
    @UpdateTimestamp
    private Date lastedUpdate;

    @ManyToOne
    @JoinColumn(name = "buyer_id"
            , foreignKey = @ForeignKey(name = "FK_PACKAGE_ORDER_BUYER")
    )
    private Account account;

    @OneToOne
    @JoinColumn(name = "transaction_id"
            , foreignKey = @ForeignKey(name = "FK_PACKAGE_ORDER_TRANSACTION")
    )
    private Transaction transaction;

    @ManyToOne
    @JoinColumn(name = "shipping_address_id"
            , foreignKey = @ForeignKey(name = "FK_PACKAGE_ORDER_SHIPPING")
    )
    private Address shippingAddress;

    @ManyToMany
    @JoinTable(
            name = "tblPackage_Order_Promotion",
            joinColumns = @JoinColumn(name = "package_order_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id"),
            foreignKey = @ForeignKey(name = "FK_PACKAGE_ORDER_PROMOTION")
    )
    private List<Promotion> promotions;

    public void addPromotion(Promotion promotion) {
        this.promotions.add(promotion);
    }
}
