package com.gangoffive.birdtradingplatform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "tblShop_Staff_Acc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShopStaff {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "staff_id")
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "account_id"
				,foreignKey = @ForeignKey(name = "FK_SHOP_STAFF_ACCOUNT")
	)
	private Account account;
	
	@ManyToOne
	@JoinColumn(name = "shop_id"
			,foreignKey = @ForeignKey(name = "FK_SHOP_STAFF_SHOP")
			)
	private ShopOwner shopOwner;
}
