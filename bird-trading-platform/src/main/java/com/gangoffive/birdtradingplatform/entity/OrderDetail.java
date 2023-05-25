package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "tblOrder_Detail")
public class OrderDetail {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_d_id")
	private Long id;
	
	@Column(name = "price"
			,nullable = false
	)
	private double price;
	@Column(name = "quantity"
			,nullable = false
	)
	private int quantity;
	
	@ManyToOne
	@JoinColumn(name = "product_id"
				,foreignKey = @ForeignKey(name = "FK_ORDER_DETAIL_PRODUCT")
				,nullable = false
	)
	private Product product;
	
	//one order have many orderdetail
	@ManyToOne
	@JoinColumn(name = "order_id"
				,foreignKey = @ForeignKey(name = "FK_ORDER_DETAIL_ORDER")
				,nullable = false
	)
	private Order order;

	@OneToOne(mappedBy = "orderDetail")
	private Review review;
	
}
