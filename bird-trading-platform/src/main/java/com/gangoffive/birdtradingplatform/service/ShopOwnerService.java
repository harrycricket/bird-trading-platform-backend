package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.BumpChartDto;

import java.util.Date;
import java.util.List;

public interface ShopOwnerService {
    List<BumpChartDto> getTotalPriceAllOrderByEachDate(String email, Date dateFrom);
}
