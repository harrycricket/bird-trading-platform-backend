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
import lombok.ToString;
@Entity(name = "tblAccount")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
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
	
	@Enumerated(value = EnumType.STRING)
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
	@Column(name = "created_date")
	private Date createdDate;
	
	@OneToOne
	@JoinColumn(name = "address_id"
				,foreignKey = @ForeignKey(name = "FK_ACCOUNT_ADDRESS")
	)
	private Address address;
	
	//one account may have one shop
	@OneToOne(mappedBy = "account")
	private ShopOwner shopOwner;
	
	
	//identify account shop staff
//	@ManyToOne
//	@JoinColumn(name = "shop_owner_id"
//	,foreignKey = @ForeignKey(name = "FK_ACCOUNT_SHOP_OWNER")
//	)
//	private ShopOwner shopOwnerId;
	
	//identify account of shop staff	
	@OneToOne(mappedBy = "account")
	private ShopStaff shopStaff;
	
	@OneToMany(mappedBy = "account")
	private List<Order> orders;
	
	@OneToMany(mappedBy = "account")
	private List<Review> reviews;
	
	//one account may have many report
	@OneToMany(mappedBy = "account")
	private List<Report> reports;
	
	//one account have many verify  token
	@OneToMany(mappedBy = "account")
	private  List<VerifyToken> verifyTokens;

	@OneToMany(mappedBy = "account")
	private List<Message> messages;

	@OneToMany(mappedBy = "account")
	private List<Notification> notifications;

	public void setId(Long id) {
		this.id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public void setShopOwner(ShopOwner shopOwner) {
		this.shopOwner = shopOwner;
	}


	public void addOrder(Order order) {
		this.orders.add(order);
	}


	public void addReview(Review review) {
		this.reviews.add(review);
	}

	public void addReport(Report report) {
		this.reports.add(report);
	}
	
	public void addVerifyToken(VerifyToken verifyToken) {
		this.verifyTokens.add(verifyToken);
	}

	public void addMessages(Message message) {
		this.messages.add(message);
	}

	public void addNotifications(Notification notification) {
		this.notifications.add(notification);
	}
}
