package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;

import com.gangoffive.birdtradingplatform.enums.ReviewRating;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ValidationMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity(name = "tblReview")
public class Review {
	@Id
	@Column(name = "review_id")
	private Long id;
	
	@Column(name = "comment",
			columnDefinition = "TEXT")
	private String comment;
	
	@Column(name = "rating")
	@Enumerated(value = EnumType.STRING)
	private ReviewRating rating;
	
	@Column(name = "img_url")
	private String imgUrl;
	
	@Column(name = "review_date")
	@CreationTimestamp
	private Date reviewDate;
	
	@OneToOne
	@JoinColumn(name = "order_product_id"
		,foreignKey = @ForeignKey(name = "FK_REVIEW_ORDER_DETAIL")
		,nullable = false
	)
	private OrderDetail orderDetail;
	
	@ManyToOne
	@JoinColumn(name ="account_id"
			,foreignKey = @ForeignKey(name = "FK_REVIEW_ACCOUNT")
	)
	private Account account;
	
	
}
