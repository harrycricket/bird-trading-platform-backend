package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.BarChartDto;
import com.gangoffive.birdtradingplatform.dto.BarChartOneTypeDto;
import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ShopOwnerService {
    List<LineChartDto> getDataLineChart(String email, Date dateFrom);

    List<PieChartDto> getDataPieChart(String email);
    List<BarChartDto> dataBarChartByPriceAllTypeProduct(String email);
    List<Order> getAllOrdersPreviousWeek(Account account);
    List<LocalDate> getAllDatePreviousWeek();
    List<BarChartDto> dataBarChartByOrderAllTypeProduct(String email);
    List<BarChartOneTypeDto> dataBarChartByPriceEachTypeProduct(
            Account account, Class<?> productClass, boolean isCalcPrice, boolean isCalcQuantity);
}
