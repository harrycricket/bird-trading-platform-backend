package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.gangoffive.birdtradingplatform.enums.TransactionStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import org.hibernate.annotations.UpdateTimestamp;

@Entity(name = "tblTransaction" )
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Transaction {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private Long id;

	@Column(name = "transaction_paypal_id")
	private String paypalId;

	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private TransactionStatus status;
	
	@Column(name = "transaction_date"
			,nullable = false
	)
	@CreationTimestamp
	private Date transactionDate;

	@Column(name = "lasted_Update")
	@UpdateTimestamp
	private Date lastedUpdate;
	
	@Column(name = "amount")
	private double amount;

	@OneToOne(mappedBy = "transaction")
	private PackageOrder packageOrder;
}
