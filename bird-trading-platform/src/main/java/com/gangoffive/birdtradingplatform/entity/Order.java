package com.gangoffive.birdtradingplatform.entity;




import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;
import com.gangoffive.birdtradingplatform.enums.PaymentMethod;
import com.gangoffive.birdtradingplatform.enums.PaymentStatus;

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
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tblOrder")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Long id;
	
	@Column(name = "total_price")
	private int totalPrice;
	
	@Column(name = "discount")
	private int discount;
	
	@Column(name = "status")
	@Enumerated(value = EnumType.STRING)
	private OrderStatus status;
	
	@Column(name = "payment_status")
	@Enumerated(value = EnumType.STRING)
	private PaymentStatus paymentStatus;
	
	@Column(name = "payment_method")
	@Enumerated(value = EnumType.STRING)
	private PaymentMethod paymentMethod;
	
	@Column(name = "created_date")
	@CreationTimestamp
	private Date createdDate;
	
	@Column(name = "is_delete")
	private Boolean delete;
	
	@ManyToOne
	@JoinColumn(name = "buyer_id"
			,foreignKey = @ForeignKey(name = "order_buyer")
	)
	private Account account; 
	
	@ManyToOne
	@JoinColumn(name = "shop_id"
	,foreignKey = @ForeignKey(name = "order_shop")
	)
	private ShopOwner shopOwner;
	
	@OneToOne
	@JoinColumn(name = "shipping_address"
	,foreignKey = @ForeignKey(name = "order_shipping")
	)
	private Address shippingAddress;
	
	@OneToOne
	@JoinColumn(name = "transaction_id"
	,foreignKey = @ForeignKey(name = "order_transaction")
	)
	private Transaction transaction;
	
}
