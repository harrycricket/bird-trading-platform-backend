package com.gangoffive.birdtradingplatform.entity;

import java.util.Date;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.gangoffive.birdtradingplatform.enums.ReviewRating;

@Getter
@Setter
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
	@Enumerated(value = EnumType.ORDINAL)
	private ReviewRating rating;
	
	@Column(name = "img_url",
			columnDefinition = "TEXT")
	private String imgUrl;
	
	@Column(name = "review_date")
	@CreationTimestamp
	private Date reviewDate;
	
	@OneToOne
	@JoinColumn(name = "order_detail_id"
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
