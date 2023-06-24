package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import com.gangoffive.birdtradingplatform.enums.ReviewRating;

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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
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
