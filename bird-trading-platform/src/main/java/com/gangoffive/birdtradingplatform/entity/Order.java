package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Entity(name = "tblOrder")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "total_price")
    private double totalPrice;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private OrderStatus status;

    @Column(name = "shipping_fee")
    private double shippingFee;

    @Column(name = "created_date")
    @CreationTimestamp
    private Date createdDate;

    @Column(name = "lasted_update")
    @UpdateTimestamp
    private Date lastedUpdate;

    @ManyToOne
    @JoinColumn(
            name = "shop_id",
            foreignKey = @ForeignKey(name = "FK_ORDER_SHOP")
    )
    private ShopOwner shopOwner;

    @ManyToOne
    @JoinColumn(
            name = "package_order_id",
            foreignKey = @ForeignKey(name = "FK_ORDER_PACKAGE_ORDER")
    )
    private PackageOrder packageOrder;

    @OneToOne
    @JoinColumn(name = "refund_transaction_id"
            , foreignKey = @ForeignKey(name = "FK_ORDER_TRANSACTION")
    )
    private Transaction transaction;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails;

    //one order may have many report
    @OneToMany(mappedBy = "order")
    private List<Report> reports;

    @OneToMany(mappedBy = "order")
    private List<LogOrder> logOrders;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastedUpdate() {
        return lastedUpdate;
    }

    public void setLastedUpdate(Date lastedUpdate) {
        this.lastedUpdate = lastedUpdate;
    }

    public ShopOwner getShopOwner() {
        return shopOwner;
    }

    public void setShopOwner(ShopOwner shopOwner) {
        this.shopOwner = shopOwner;
    }

    public PackageOrder getPackageOrder() {
        return packageOrder;
    }

    public void setPackageOrder(PackageOrder packageOrder) {
        this.packageOrder = packageOrder;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
    }

    public List<Report> getReports() {
        return reports;
    }

    public void addReports(Report report) {
        this.reports.add(report);
    }
}
