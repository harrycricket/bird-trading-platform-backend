package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Entity(name = "tblAddress")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id", updatable = false)
	private Long id;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "phone")
	private String phone;
	
	@Column(name = "address")
	private String address;

	@Column(name = "last_updated")
	@UpdateTimestamp
	private Date lastUpdated;
	
	@OneToOne(mappedBy = "address")
	private Account account;

	@OneToOne(mappedBy = "address")
	private ShopOwner shopOwner;
	
	@OneToOne(mappedBy = "shippingAddress")
	private PackageOrder packageOrder;

}
