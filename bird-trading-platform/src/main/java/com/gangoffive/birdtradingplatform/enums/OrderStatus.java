package com.gangoffive.birdtradingplatform.enums;

import com.gangoffive.birdtradingplatform.exception.CustomRuntimeException;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

public enum OrderStatus {
    PENDING(0, "The order has been placed but not yet processed"),
    PROCESSING(1, "The order is being prepared for shipment"),
    SHIPPED(2, "The order has been shipped or handed over to the shipping carrier"),
    SHIPPING(3, "The order is currently in transit and is on its way to you."),
    DELIVERED(4, "The order has been successfully delivered to you"),
    CANCELLED(-1, "The order has been cancelled."),
    REFUNDED(-2, "The order has been refunded.");
    private int statusCode;
    private String description;

    OrderStatus(int statusCode, String description) {
        this.statusCode = statusCode;
        this.description = description;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getDescription() {
        return description;
    }

    public static OrderStatus getOrderStatusBaseOnStatusCode(int statusCode) {
        List<OrderStatus> listStatus = Arrays.asList(OrderStatus.values());
        try {
            OrderStatus result = listStatus.stream().filter(sta -> sta.getStatusCode() == statusCode).findFirst().get();
            return result;
        } catch (Exception e) {
            throw new CustomRuntimeException(HttpStatus.NOT_FOUND.name(), "Not found this status");
        }
    }
}
//        Pending: The order has been placed but not yet processed.
//        Processing: The order is being prepared for shipment.
//        Shipped: The order has been shipped or handed over to the shipping carrier.
//        In Transit: The order is in transit and on its way to the customer.
//        Out for Delivery: The order is out for delivery and expected to be delivered soon.
//        Delivered: The order has been successfully delivered to the customer.
//        Cancelled: The order has been cancelled, either by the customer or by the system.
//        Refunded: The order has been refunded, indicating a reversal of payment.