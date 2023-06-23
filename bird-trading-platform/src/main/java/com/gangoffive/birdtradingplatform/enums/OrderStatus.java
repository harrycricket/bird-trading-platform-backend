package com.gangoffive.birdtradingplatform.enums;

public enum OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    SHIPPING,
    DELIVERED,
	CANCELLED,
    REFUNDED
}
//Pending: The order has been placed but not yet processed.
//        Processing: The order is being prepared for shipment.
//        Shipped: The order has been shipped or handed over to the shipping carrier.
//        In Transit: The order is in transit and on its way to the customer.
//        Out for Delivery: The order is out for delivery and expected to be delivered soon.
//        Delivered: The order has been successfully delivered to the customer.
//        Cancelled: The order has been cancelled, either by the customer or by the system.
//        Refunded: The order has been refunded, indicating a reversal of payment.