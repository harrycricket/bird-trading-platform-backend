package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;

import java.util.Date;
import java.util.List;

public interface ShopOwnerService {
    List<LineChartDto> getDataLineChart(String email, Date dateFrom);

    List<PieChartDto> getDataPieChart(String email);
    DataBarChartDto dataBarChartByPriceAllTypeProduct(String email);
    DataBarChartDto dataBarChartByOrderAllTypeProduct(String email);
    DataBarChartDto dataBarChartByReviewAllTypeProduct(String email);
    List<Order> getAllOrdersNumberPreviousWeek(Account account, int week);
    List<BarChartOneTypeDto> dataBarChartEachTypeProduct(
            Account account, Class<?> productClass, boolean isCalcPrice, boolean isCalcQuantity, boolean isCalcReview, int week);
    String redirectToShopOwner(String email);
}
