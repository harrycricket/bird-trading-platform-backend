package com.gangoffive.birdtradingplatform.common;

public class NotifiConstant {
    public static final long TIME_BEFOR_NOTI_LOAD =  7 * 24 * 60 * 60 * 1000;
    public static final String NOTI_SHOP_ROLE = "shop";
    public static final String NOTI_USER_ROLE = "user";
    public static final String NOTI_EVERYONE_ROLE = "everyone";
    public static final String NOTIFICATION_SEND = "Announcement to %s with message %s";
    public static final String NOTIFICATION_PUBLIC = "Announcement to everyone with message %s";

    //ORDER
    public static final String ORDER_NAME_NOTI_USER = "YOUR ORDER";
    public static final String NEW_ORDER_FOR_SHOP_OWNER_NAME = "NEW ORDER";
    public static final String NEW_ORDER_FOR_SHOP_OWNER_CONTENT = "Congratulations! You have a new order.";

    public static final String ORDER_SUCCESS_DELIVERED_TO_CUSTOMER = "Order with ID %d successfully DELIVERED to the customer!";

    //BAN
    public static final String BAN_SHOP_FOR_USER_NAME = "YOUR SHOP";

    //PROMOTION
    public static final String NEW_PROMOTION_NAME = "NEW VOUCHER";
    public static final String NEW_PROMOTION_CONTENT = "New voucher for %s available from %s to %s," +
            " exclusively for the first %d orders!";

    //REIVEW
    public static final String NEW_REVIEW_FOR_SHOP = "NEW REVIEW";

    public static final String NEW_REVIEW_FOR_SHOP_CONTENT = "You have received a new review on product ID %d!";
}
