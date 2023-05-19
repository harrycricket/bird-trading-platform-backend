package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tblOrder")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "total_price")
    private int totalPrice;

    @Column(name = "discount")
    private int discount;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @Column(name = "payment_status")
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_method")
    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "created_date")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "is_delete")
    private Boolean delete;

    @ManyToOne
    @JoinColumn(name = "buyer_id",
             foreignKey = @ForeignKey(name = "FK_ORDER_BUYER")
    )
    private Account account;

    @ManyToOne
    @JoinColumn(name = "shop_id",
             foreignKey = @ForeignKey(name = "FK_ORDER_SHOP")
    )
    private ShopOwner shopOwner;

    @OneToOne
    @JoinColumn(name = "shipping_address",
             foreignKey = @ForeignKey(name = "FK_ORDER_SHIPPING")
    )
    private Address shippingAddress;

    @OneToOne
    @JoinColumn(name = "transaction_id",
             foreignKey = @ForeignKey(name = "FK_ORDER_TRANSACTION")
    )
    private Transaction transaction;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    @ManyToMany
    @JoinTable(
            name = "tblOrder_Promotion",
            joinColumns = @JoinColumn(name = "order_id"),
            foreignKey = @ForeignKey(name = "FK_ORDER_PROMOTION"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id")
    )
    private List<Promotion> promotions;

    @ManyToMany
    @JoinTable(
            name = "tblOrder_Promotion_Shop",
            joinColumns = @JoinColumn(name = "order_id"),
            foreignKey = @ForeignKey(name = "FK_ORDER_PROMOTIONSHOP"),
            inverseJoinColumns = @JoinColumn(name = "promotion_s_id")
    )
    private List<PromotionShop> promotionShops;

    public void setId(Long id) {
        this.id = id;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    public void setShopOwner(ShopOwner shopOwner) {
        this.shopOwner = shopOwner;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public void addOrderDetails(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
    }

    public void addPromotionShop(PromotionShop promotionShop) {
        this.promotionShops.add(promotionShop);
    }
    public void addPromotion(Promotion promotion) {
        this.promotions.add(promotion);
    }

}
