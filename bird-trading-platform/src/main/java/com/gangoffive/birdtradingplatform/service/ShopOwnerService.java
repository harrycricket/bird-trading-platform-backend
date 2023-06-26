package com.gangoffive.birdtradingplatform.service;


import com.gangoffive.birdtradingplatform.dto.BarChartOneTypeDto;
import com.gangoffive.birdtradingplatform.dto.DataBarChartDto;
import com.gangoffive.birdtradingplatform.dto.LineChartDto;
import com.gangoffive.birdtradingplatform.dto.PieChartDto;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;

public interface ShopOwnerService {

//    List<Product>

    List<String> listShopDto(List<Long> listShopId, long userId);

    long getAccountIdByShopid(long shopId);

    List<LineChartDto> getDataLineChart(String dateFrom, int date);

    List<PieChartDto> getDataPieChart();

    DataBarChartDto dataBarChartByPriceAllTypeProduct();

    DataBarChartDto dataBarChartByOrderAllTypeProduct();

    DataBarChartDto dataBarChartByReviewAllTypeProduct();

    List<Order> getAllOrdersNumberPreviousWeek(Account account, int week);

    List<BarChartOneTypeDto> dataBarChartEachTypeProduct(
            Account account, Class<?> productClass, boolean isCalcPrice, boolean isCalcQuantity, boolean isCalcReview, int week);

    ResponseEntity<?> redirectToShopOwner();

    ResponseEntity getShopInforByUserId();

    long getShopIdByEmail(String email);
}
