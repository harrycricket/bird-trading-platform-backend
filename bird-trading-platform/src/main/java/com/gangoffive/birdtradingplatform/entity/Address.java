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
	
	@Column(name = "line"
			,nullable = false
	)
	private String line;
	
	@Column(name = "district"
			,nullable = false
	)
	private String district;

	@Column(name = "city"
			,nullable = false
	)
	private String city;
	
	@Column(name = "country"
			,nullable = false
	)
	private String country;
	
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
	private List<Order> order;
	
	
}
