package com.gangoffive.birdtradingplatform.dto;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


public class AccountDto {
	
	private Long id;
	
	private String email;

	public AccountDto(Long id, String email) {
		super();
		this.id = id;
		this.email = email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "AccountDto [id=" + id + ", email=" + email + "]";
	}
	
	
}
