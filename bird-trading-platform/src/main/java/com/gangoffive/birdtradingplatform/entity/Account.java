package com.gangoffive.birdtradingplatform.entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import com.gangoffive.birdtradingplatform.enums.AuthProvider;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity(name = "tblAccount")
@Builder
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

	@Column(name = "password")
	private String password;
	
	@Enumerated(value = EnumType.STRING)
	private UserRole role;
	
	@Column(name = "refresh_token")
	private String refreshToken;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private AccountStatus status;
	
	@Column(name = "full_name")
	private String fullName;
	
	@Column(name = "img_url")
	private String imgUrl;

	@Column(name = "phone_number")
	private String phoneNumber;
	
	@CreationTimestamp
	@Column(name = "created_date")
	private Date createdDate;

//	@NotNull
	@Enumerated(EnumType.STRING)
	private AuthProvider provider;

	private String providerId;
	
	@OneToOne
	@JoinColumn(name = "address_id"
				,foreignKey = @ForeignKey(name = "FK_ACCOUNT_ADDRESS")
	)
	private Address address;
	
	//one account may have one shop
	@OneToOne(mappedBy = "account")
	private ShopOwner shopOwner;

	
	@OneToMany(mappedBy = "account")
	private List<PackageOrder> packageOrders;
	
	@OneToMany(mappedBy = "account")
	private List<Review> reviews;
	
	//one account may have many report
	@OneToMany(mappedBy = "account")
	private List<Report> reports;
	
	//one account have many verify  token
	@OneToMany(mappedBy = "account")
	private  List<VerifyToken> verifyTokens;

	@OneToMany(mappedBy = "account")
	private List<Notification> notifications;

	@OneToMany(mappedBy = "account")
	private List<Channel> channels;

	public void setId(Long id) {
		this.id = id;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRole(UserRole role) {
		this.role = role;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
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


	public void addPackageOrder(PackageOrder packageOrder) {
		this.packageOrders.add(packageOrder);
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

	public void addNotifications(Notification notification) {
		this.notifications.add(notification);
	}

	public AuthProvider getProvider() {
		return provider;
	}

	public void setProvider(AuthProvider provider) {
		this.provider = provider;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public List<Channel> getChannels() {
		return channels;
	}

	public void setChannels(List<Channel> channels) {
		this.channels = channels;
	}
}
