package com.gangoffive.kafkaandws.dto;

import java.util.Date;

public class NotificationDto {
    private long id;
    private String notiText;
    private String name;
    private boolean isSeen;
    private String role;
    private Date notiDate;
    private long receiveId;

    public NotificationDto() {
    }

    public NotificationDto(long id, String notiText, String name, boolean isSeen, String role, Date notiDate, long receiveId) {
        this.id = id;
        this.notiText = notiText;
        this.name = name;
        this.isSeen = isSeen;
        this.role = role;
        this.notiDate = notiDate;
        this.receiveId = receiveId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNotiText() {
        return notiText;
    }

    public void setNotiText(String notiText) {
        this.notiText = notiText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getNotiDate() {
        return notiDate;
    }

    public void setNotiDate(Date notiDate) {
        this.notiDate = notiDate;
    }

    public long getReceiveId() {
        return receiveId;
    }

    public void setReceiveId(long receiveId) {
        this.receiveId = receiveId;
    }

    @Override
    public String toString() {
        return "NotificationDto{" +
                "id=" + id +
                ", notiText='" + notiText + '\'' +
                ", name='" + name + '\'' +
                ", isSeen=" + isSeen +
                ", role='" + role + '\'' +
                ", notiDate=" + notiDate +
                ", receiveId=" + receiveId +
                '}';
    }
}
