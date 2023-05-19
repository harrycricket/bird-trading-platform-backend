package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "tblNotification")
@NoArgsConstructor
@AllArgsConstructor
@Getter
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
	
	@Column(name = "is_seen")
	private boolean seen;

	@Column(name = "is_shop_receive")
	private boolean shopReceive;

	@ManyToOne
	@JoinColumn(name = "receiver_id")
	private Account account;


	
}
