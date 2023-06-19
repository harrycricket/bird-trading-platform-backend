package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity(name = "tblChannel")
@AllArgsConstructor
@NoArgsConstructor
public class Channel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "channel_id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "create_at")
    @CreationTimestamp
    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "account_id",
    foreignKey = @ForeignKey(name = "FK_CHANNEL_ACCOUNT"))
    private Account account;

    @ManyToOne
    @JoinColumn(name = "shop_id",
    foreignKey = @ForeignKey(name = "FK_CHANNEL_SHOP_OWNER"))
    private ShopOwner shopOwner;

    @OneToMany(mappedBy = "channel")
    private List<Message> messages;

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

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ShopOwner getShopOwner() {
        return shopOwner;
    }

    public void setShopOwner(ShopOwner shopOwner) {
        this.shopOwner = shopOwner;
    }
}
