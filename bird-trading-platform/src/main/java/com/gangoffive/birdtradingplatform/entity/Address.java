package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;

@Entity
public class Address {
	@SequenceGenerator(name = "addredd_sequence", sequenceName = "addredd_sequence", allocationSize = 1)
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "addredd_sequence")
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
}
