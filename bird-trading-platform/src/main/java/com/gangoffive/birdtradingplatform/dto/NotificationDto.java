package com.gangoffive.birdtradingplatform.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationDto {
    private String notiText;
    private boolean isSeen;
    private String role;
    private Date notiDate;
    private long receiveId;
}
