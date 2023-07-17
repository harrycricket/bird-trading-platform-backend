package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageDto {
    private long id;
    private String senderName;
    private String userName;
    private long userID;
    private long shopID;
    private String content;
    private Date date;
    private String status;
    private String userAvatar;
}
