package com.gangoffive.birdtradingplatform.entity;

import com.gangoffive.birdtradingplatform.enums.AccountStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "tblShop_Staff_Acc")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopStaff {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "staff_id")
	private Long id;

	@Column(name = "user_name",
	nullable = false)
	private String userName;

	@Column(name = "password",
	nullable = false)
	private String password;

	@Column(name = "status",
	nullable = false)
	@Enumerated(EnumType.STRING)
	private AccountStatus status;

	@ManyToOne
	@JoinColumn(name = "shop_id"
			,foreignKey = @ForeignKey(name = "FK_SHOP_STAFF_SHOP")
			)
	private ShopOwner shopOwner;
}
