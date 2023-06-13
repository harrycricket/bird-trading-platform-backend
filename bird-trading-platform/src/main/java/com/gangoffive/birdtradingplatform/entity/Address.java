package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tblAddress")
public class Address {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "address_id", updatable = false)
	private Long id;
	
	@Column(name = "street"
			,nullable = false
	)
	private String street;
	
	@Column(name = "ward"
			,nullable = false
	)
	private String ward;

	@Column(name = "district"
			,nullable = false
	)
	private String district;
	
	@Column(name = "city"
			,nullable = false
	)
	private String city;
	
	@Column(name = "phone"
			,nullable = false
	)
	private String phone;
	
	@Column(name = "last_updated")
	@UpdateTimestamp
	private Date lastUpdated;
	
	@OneToOne(mappedBy = "address")
	private Account account;
	
	@OneToMany(mappedBy = "shippingAddress")
	private List<PackageOrder> packageOrders;

	public void addPackageOrder(PackageOrder packageOrder) {
		packageOrders.add(packageOrder);
	}
}
