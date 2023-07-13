package com.gangoffive.kafkaandws.constant;


import java.util.Arrays;
import java.util.List;

public class MessageConstant {
    public static final int CHANNEL_PAGING_SIZE = 10;
    public static final String MESSAGE_STATUS_SENT = "sent";
    public static final String MESSAGE_SHOP_ROLE = "shop";
    public static final String MESSAGE_USER_ROLE = "user";
    public static final String MESSAGE_SEND_LOG = "Message send to %d with content %s in destination %s";

    public static final String MESSAGE_RECEIVE_LOG = "Message receive by %s with content send to %s";
}
