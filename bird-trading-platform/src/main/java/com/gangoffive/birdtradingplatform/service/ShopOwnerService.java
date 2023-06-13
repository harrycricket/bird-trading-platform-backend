package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.entity.Account;

import java.util.Date;
import java.util.List;

public interface ShopOwnerService {
    List<LineChartDto> getDataLineChart(String email, Date dateFrom);
    List<PieChartDto> getDataPieChart(String email);
    double dataPieChartByTypeProduct(Account account, Class<?> productClass);
}
