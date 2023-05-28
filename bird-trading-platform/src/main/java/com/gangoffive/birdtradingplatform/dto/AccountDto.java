package com.gangoffive.birdtradingplatform.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
	private Long id;

	private String email;

	private String password;

	private String phoneNumber;

	private String matchingPassword;

	private String fullName;

}
