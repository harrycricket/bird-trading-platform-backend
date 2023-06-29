package com.gangoffive.birdtradingplatform.common;

import com.gangoffive.birdtradingplatform.enums.OrderStatus;

import java.util.Arrays;
import java.util.List;

public class OrderStatusConstant {
    public static List<OrderStatus> VIEW_ORDER_STATUS =
            Arrays.asList(OrderStatus.PENDING, OrderStatus.PROCESSING, OrderStatus.SHIPPED,
                    OrderStatus.SHIPPING, OrderStatus.DELIVERED, OrderStatus.CANCELLED, OrderStatus.REFUNDED);
    public static List<OrderStatus> UPDATE_ORDER_STATUS_USER =
            Arrays.asList(OrderStatus.CANCELLED);
    //co cancel hay k
    public static List<OrderStatus> UPDATE_ORDER_STATUS_SHOP_OWNER =
            Arrays.asList(OrderStatus.PENDING, OrderStatus.PROCESSING, OrderStatus.SHIPPED);
    public static List<OrderStatus> UPDATE_ORDER_STATUS_SHOP_STAFF =
            Arrays.asList(OrderStatus.PENDING, OrderStatus.PROCESSING, OrderStatus.SHIPPED);
    public static List<OrderStatus> UPDATE_ORDER_STATUS_ADMIN =
            Arrays.asList(OrderStatus.REFUNDED);
    public static List<OrderStatus> UPDATE_ORDER_STATUS_SHIP =
            Arrays.asList(OrderStatus.SHIPPING, OrderStatus.DELIVERED, OrderStatus.CANCELLED);
}
