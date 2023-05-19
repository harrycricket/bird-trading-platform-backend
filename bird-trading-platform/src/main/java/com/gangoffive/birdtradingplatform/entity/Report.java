package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity(name = "tblReport")

public class Report {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "report_id")
	private Long id;
	
	@Column(name = "report_text"
			,nullable = false
			)
	private String reportText;
	
	@Column(name = "report_date"
			,nullable = false
			)
	@CreationTimestamp
	private Date reportDate;
	
	@ManyToOne
	@JoinColumn(name = "account_id"
				,foreignKey = @ForeignKey(name = "FK_REPORT_ACCOUNT"))
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "order_id"
				,foreignKey = @ForeignKey(name = "FK_REPORT_ORDER")
	)
	private Order order;
}
