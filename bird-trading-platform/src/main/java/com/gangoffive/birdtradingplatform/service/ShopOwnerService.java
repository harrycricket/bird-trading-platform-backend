package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface ShopOwnerService {
    List<LineChartDto> getDataLineChart(String email, Date dateFrom);

    List<PieChartDto> getDataPieChart(String email);
    DataBarChartDto dataBarChartByPriceAllTypeProduct(String email);
    DataBarChartDto dataBarChartByOrderAllTypeProduct(String email);
    List<Order> getAllOrdersNumberPreviousWeek(Account account, int week);
    List<LocalDate> getAllDatePreviousWeek(int week);

    List<BarChartOneTypeDto> dataBarChartByPriceEachTypeProduct(
            Account account, Class<?> productClass, boolean isCalcPrice, boolean isCalcQuantity, int week);
    String redirectToShopOwner(String email);
}
