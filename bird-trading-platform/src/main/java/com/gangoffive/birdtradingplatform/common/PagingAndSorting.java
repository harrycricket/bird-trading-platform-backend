package com.gangoffive.birdtradingplatform.common;

import org.springframework.data.domain.Sort;

public class PagingAndSorting {
    public static final int DEFAULT_PAGE_SIZE = 8;
    public static final int DEFAULT_PAGE_SHOP_SIZE = 10;
    public static final Integer DEFAULT_PAGE_NUMBER = 1;
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.DESC;
    public static final int DEFAULT_PAGE_MESSAGE_SIZE = 100;

    public static final int HIGHEST_PRICE_FILTER = 999999999;
    public static final int LOWEST_PRICE_FILTER = -1;
    public static final double DEFAULT_STAR_FILTER = 0.0;
}
