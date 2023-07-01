package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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

	@Column(name = "product_promotion_rate")
	private double productPromotionRate;
	
	@ManyToOne
	@JoinColumn(name = "product_id"
				,foreignKey = @ForeignKey(name = "FK_ORDER_DETAIL_PRODUCT")
				,nullable = false
	)
	private Product product;

	@ManyToOne
	@JoinColumn(name = "order_id"
				,foreignKey = @ForeignKey(name = "FK_ORDER_DETAIL_ORDER")
				,nullable = false
	)
	private Order order;

	@OneToOne(mappedBy = "orderDetail")
	private Review review;

	@ManyToMany
	@JoinTable(
			name = "tblOrder_Detail_Promotion_Shop",
			foreignKey = @ForeignKey(name = "FK_ORDER_DETAIL_PROMOTION_SHOP"),
			joinColumns = @JoinColumn(name = "order_d_id"),
			inverseJoinColumns = @JoinColumn(name = "promotion_s_id")
	)
	private List<PromotionShop> promotionShops;

	public List<PromotionShop> getPromotionShops() {
		return promotionShops;
	}

	public void addPromotionShops(PromotionShop promotionShop) {
		this.promotionShops.add(promotionShop);
	}
}
