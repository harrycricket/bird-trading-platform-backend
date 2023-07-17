package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.dto.OrderStatusDto;
import com.gangoffive.birdtradingplatform.dto.PromotionShopDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum FieldOrderTable {
    ID("id"),
    PACKAGE_ORDER_ID("packageOrderId"),
    SHOP_ID("shopId"),
    ORDER_STATUS("orderStatus"),
    PAYMENT_METHOD("paymentMethod"),
    PROMOTION_SHOP("promotionsShop"),
    TOTAL_PRICE("totalPrice"),
    SHIPPING_FEE("shippingFee"),
    CREATED_DATE("createdDate"),
    LASTED_UPDATE("lastedUpdate");
    private String field;
}
