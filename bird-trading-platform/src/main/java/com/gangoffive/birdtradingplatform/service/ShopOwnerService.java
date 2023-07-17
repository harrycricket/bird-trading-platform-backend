package com.gangoffive.birdtradingplatform.service;


import com.gangoffive.birdtradingplatform.dto.*;
import com.gangoffive.birdtradingplatform.entity.Account;
import com.gangoffive.birdtradingplatform.entity.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ShopOwnerService {
    long getAccountIdByShopId(long shopId);

    List<LineChartDto> getDataLineChart(String dateFrom, int date);

    List<PieChartDto> getDataPieChart();

    DataBarChartDto dataBarChartByPriceAllTypeProduct();

    DataBarChartDto dataBarChartByOrderAllTypeProduct();

    DataBarChartDto dataBarChartByReviewAllTypeProduct();

    List<Order> getAllOrdersNumberPreviousWeek(Account account, int week);

    List<BarChartOneTypeDto> dataBarChartEachTypeProduct(
            Account account, Class<?> productClass, boolean isCalcPrice, boolean isCalcQuantity, boolean isCalcReview, int week);

    ResponseEntity<?> redirectToShopOwner();

    ResponseEntity<?> getShopInfoByUserId();

    long getShopIdByEmail(String email);

    ResponseEntity<?> createAccountStaff(CreateAccountSaffDto createAccountSaffDto);

    ResponseEntity<?> getShopStaff(int pageNumber);

    ResponseEntity<?> updateShopOwnerProfile(MultipartFile avatarImg, MultipartFile coverImg, ShopOwnerUpdateDto shopInfoDto);

    ResponseEntity<?> filterAllShopOwner(ShopOwnerAccountFilterDto shopOwnerAccountFilter);

    ResponseEntity<?> updateListShopOwnerAccountStatus(ChangeStatusListIdDto changeStatusListIdDto);
}
