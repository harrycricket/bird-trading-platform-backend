package com.gangoffive.birdtradingplatform.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;
@Entity(name = "tblShop_Owner_Acc")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class ShopOwner {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shop_id",
			nullable = false
	)
	private Long id;
	
	@Column(name = "shop_name"
			,nullable = false
	)
	private String shopName;
	
	@Column(name = "shope_phone"
			,nullable = false
			,unique = true
	)
	private String shopPhone;
	
	@Column(name = "description",
			columnDefinition = "TEXT")
	private String description;
	
	@Column(name = "img_url")
	private String imgUrl;
	
	@Column(name = "active")
	private Boolean active;
	
	@Column(name = " created_date")
	@CreationTimestamp
	private Date createdDate;
	
	// one shop have one account
	@OneToOne
	@JoinColumn(name = "account_id"
				,foreignKey = @ForeignKey(name = "FK_SHOP_ACCOUNT")
	)
	private Account account;
	
//	one shop have many staff account
//	@OneToMany(mappedBy = "shopOwnerId")
//	private List<Account> shopStaffAccounts;
	
	//one shop may have MANY staff account
	@OneToMany(mappedBy = "shopOwner")
	private List<ShopStaff> shopStaffAccount;
	
	//one shop have many ordes
	@OneToMany(mappedBy = "shopOwner")
	private List<Order> orders;

    @OneToMany(mappedBy = "shopOwner")
    private List<Product> products;

	@OneToMany(mappedBy = "shopOwner")
	private List<PromotionShop> promotionShopList;

    public void setId(Long id) {
        this.id = id;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public void setShopPhone(String shopPhone) {
        this.shopPhone = shopPhone;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public void addShopStaffAccount(ShopStaff shopStaffAccount) {
        this.shopStaffAccount.add(shopStaffAccount);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }

    public void addProducts(Product product) {
        this.products.add(product);
    }

	public List<PromotionShop> getPromotionShopList() {
		return promotionShopList;
	}

	public void setPromotionShopList(PromotionShop promotionShopList) {
		this.promotionShopList.add(promotionShopList);
	}

}
