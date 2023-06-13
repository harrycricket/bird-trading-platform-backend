package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.LineChartDto;

import java.util.Date;
import java.util.List;

public interface ShopOwnerService {
    List<LineChartDto> getTotalPriceAllOrderByEachDate(String email, Date dateFrom);
}
