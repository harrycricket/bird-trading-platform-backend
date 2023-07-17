package com.gangoffive.kafkaandws.dto;


import lombok.Data;

import java.util.Date;

@Data

public class MessageDto {
    private long id;
    private String senderName;
    private String userName;
    private String userAvatar;
    private long userID;
    private long shopID;
    private String content;
    private Date date;
    private String status;
}
