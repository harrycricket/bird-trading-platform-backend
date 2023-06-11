package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "tblOrder_Detail")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
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
