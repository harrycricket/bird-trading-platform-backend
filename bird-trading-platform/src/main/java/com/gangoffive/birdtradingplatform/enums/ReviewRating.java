package com.gangoffive.birdtradingplatform.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ReviewRating {
	ONE_STAR(1),
	TWO_STAR(2),
	THREE_STAR(3),
	FOUR_STAR(4),
	FIVE_STAR(5);
	private int star;
}
