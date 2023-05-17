package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tblTransaction" )
public class Transaction {
	@Id
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	@Column(name = "transaction_id")
	private Long id;
	
	@Column(name = "transaction_date"
			,nullable = false
	)
	@CreationTimestamp
	private Date transactionDate;
	
	@Column(name = "amount")
	private String amount;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private TransactionStatus status;
	
	@OneToOne(mappedBy = "transaction")
	private Order order;
}
