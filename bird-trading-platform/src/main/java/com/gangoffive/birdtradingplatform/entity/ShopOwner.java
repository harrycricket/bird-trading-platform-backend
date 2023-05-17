package com.gangoffive.birdtradingplatform.entity;


import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;

@Entity(name = "tblShop_Owner_Acc")
//@Entity
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
	
	@Column(name = "description")
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
				,foreignKey = @ForeignKey(name = "shop_account")
	)
	private Account account;
	
//	one shop have many staff account
	@OneToMany(mappedBy = "shopOwnerId")
	private List<Account> shopStaffAccount;
	
	//one shop have many ordes
	@OneToMany(mappedBy = "shopOwner")
	private List<Order> orders;
}
