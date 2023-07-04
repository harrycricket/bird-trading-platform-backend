package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.util.Date;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//@Where(clause = "is_deleted = false AND quantity > 0")
//@EntityListeners(ProductEntityListener.class)
public abstract class Product {
    @Id
    @Column(name = "product_id")
    @SequenceGenerator(
            name = "product_id_seq",
            sequenceName = "product_id_seq",
            allocationSize = 1,
            initialValue = 1
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

    @Column(nullable = false,
            columnDefinition = "TEXT")
    protected String description;

    @Column(name = "created_date")
    @CreationTimestamp
    protected Date createdDate;

    @Column(name = "last_updated")
    @UpdateTimestamp
    protected Date lastUpDated;

    @Column(nullable = false)
    protected Integer quantity;

    @Column(name = "img_url", nullable = false,
            columnDefinition = "TEXT")
    protected String imgUrl;

    @Column(name = "video_url",
            columnDefinition = "TEXT")
    protected String videoUrl;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    protected ProductStatus status;

    @OneToMany(mappedBy = "product")
    private List<OrderDetail> orderDetails;

    @ManyToOne
    @JoinColumn(
            name = "shop_id",
            foreignKey = @ForeignKey(name = "FK_PRODUCT_SHOP_OWNER")
    )
    protected ShopOwner shopOwner;
    @ManyToMany(mappedBy = "products")
    protected List<PromotionShop> promotionShops;

    @OneToOne(mappedBy = "product")
    protected ProductSummary productSummary;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpDated() {
        return lastUpDated;
    }

    public void setLastUpDated(Date lastUpDated) {
        this.lastUpDated = lastUpDated;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public ShopOwner getShopOwner() {
        return shopOwner;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetails.add(orderDetail);
    }

    public void setShopOwner(ShopOwner shopOwner) {
        this.shopOwner = shopOwner;
    }

    public List<PromotionShop> getPromotionShops() {
        return promotionShops;
    }

    public void addPromotionShop(PromotionShop promotionShop) {
        this.promotionShops.add(promotionShop);
    }

    public ProductSummary getProductSummary() {
        return productSummary;
    }

    public void setProductSummary(ProductSummary productSummary) {
        this.productSummary = productSummary;
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", price=" + price + ", description=" + description
                + ", createdDate=" + createdDate + ", lastUpDated=" + lastUpDated + ", quantity=" + quantity
                + ", imgUrl=" + imgUrl + ", videoUrl=" + videoUrl + "]";
    }
}
