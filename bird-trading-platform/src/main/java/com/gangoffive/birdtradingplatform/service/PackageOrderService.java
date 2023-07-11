package com.gangoffive.birdtradingplatform.service;

import com.gangoffive.birdtradingplatform.dto.*;
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

    ResponseEntity<?> filterAllPackageOrder(PackageOrderAdminFilterDto packageOrderFilter);
}
