package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tblNotification")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "noti_id")
	private Long id;
	
	@Column(name = "noti_text")
	private String notiText;
	
	@CreationTimestamp
	@Column(name = "noti_date")
	private Date notiDate;
	
	@Column(name = "seen")
	private Boolean seen;
	
	
}
