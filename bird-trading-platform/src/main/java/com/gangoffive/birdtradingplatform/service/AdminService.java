package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.DataBumpChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.entity.Order;

import java.util.List;

public interface AdminService {
    DataBumpChartDto dataBumpChartRankOfShop();
    List<PieChartDto> dataPieChartRankOfShop();
    List<Order> getAllOrdersNumberPreviousWeek(int startWeekBefore, int endWeekBefore);
}
