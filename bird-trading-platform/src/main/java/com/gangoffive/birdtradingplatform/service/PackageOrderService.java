package com.gangoffive.birdtradingplatform.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gangoffive.birdtradingplatform.dto.ItemByShopDto;
import com.gangoffive.birdtradingplatform.dto.PackageOrderRequestDto;
import com.gangoffive.birdtradingplatform.dto.TotalOrderDto;
import com.gangoffive.birdtradingplatform.dto.UserOrderDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface PackageOrderService {
    ResponseEntity<?> packageOrder(PackageOrderRequestDto packageOrder, String paymentId, String payerId);

    boolean checkPromotion(PackageOrderRequestDto packageOrder, Map<Long, Integer> productOrder);

    boolean checkTotalShopPrice(List<ItemByShopDto> itemsByShop);

    boolean checkSubTotal(double subTotal, Map<Long, Integer> productOrder);

    boolean checkTotalShippingFee(PackageOrderRequestDto packageOrder);

    boolean checkTotalDiscount(PackageOrderRequestDto packageOrder);

    boolean checkTotalPayment(TotalOrderDto totalOrderDto);

    boolean checkListProduct(Map<Long, Integer> productOrder);

    boolean checkUserOrderDto(UserOrderDto userOrder);

    ResponseEntity<?> viewAllPackageOrderByAccountId(int pageNumber);
    double getShippingFeeByDistance(double distance) throws JsonProcessingException;
}
