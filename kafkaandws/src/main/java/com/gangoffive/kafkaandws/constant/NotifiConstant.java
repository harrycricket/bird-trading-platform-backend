package com.gangoffive.kafkaandws.constant;

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
}
