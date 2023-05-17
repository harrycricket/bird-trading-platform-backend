package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.gangoffive.birdtradingplatform.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tblAccount")
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "account_id"
			,updatable = false
	)
	private Long id;
	
	@Column(name = "email",
			unique = true,
			nullable = false
	)
	private String email;
	
	@Enumerated(value  = EnumType.STRING)
	private UserRole role;
	
	@Column(name = "refresh_token")
	private String refreshToken;
	
	@Column(name = "status")
	private Boolean enable;
	
	@Column(name = "first_name")
	private String firstName;
	
	@Column(name = "last_name")
	private String lastName;
	
	@Column(name = "img_url")
	private String imgUrl;
	
	@CreationTimestamp
	private Date createdDate;
	
	@OneToOne
	@JoinColumn(name = "address_id"
				,foreignKey = @ForeignKey(name = "account_address")
	)
	private Address address;
	
	//one account may have one shop
	@OneToOne(mappedBy = "account")
	private ShopOwner shopOwner;
	
	
	//identify account shop staff
	@ManyToOne
	@JoinColumn(name = "shop_owner_id"
	,foreignKey = @ForeignKey(name = "account_shoponwer")
	)
	private ShopOwner shopOwnerId;
	
	@OneToMany(mappedBy = "account")
	private List<Order> orders;
	
}
