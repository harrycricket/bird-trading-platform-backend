package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity(name = "tblVerify_Token")
@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VerifyToken {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "token_id")
	private Long id;
	
	@Column(name = "token"
			,nullable = false
			)
	private String token;
	
	@Column(name = "expired"
			,nullable = false
			)
	private Date expired;

	private boolean revoked;
	
	@ManyToOne
	@JoinColumn(name = "account_id"
			,foreignKey = @ForeignKey(name = "FK_VERIFY_TOKEN_ACCOUNT"))
	private Account account;
	
}
