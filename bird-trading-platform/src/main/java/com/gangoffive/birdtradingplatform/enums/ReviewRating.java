package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ReviewRating {
	NO_STAR(0),
	ONE_STAR(1),
	TWO_STAR(2),
	THREE_STAR(3),
	FOUR_STAR(4),
	FIVE_STAR(5);
	private int star;

	public static ReviewRating getReviewRatingByStar(int star) {
		return Arrays.stream(ReviewRating.values())
				.filter(reviewRating -> reviewRating.getStar() == star)
				.findFirst()
				.get();
	}
}
