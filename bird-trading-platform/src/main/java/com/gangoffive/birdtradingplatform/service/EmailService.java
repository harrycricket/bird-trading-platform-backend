package com.gangoffive.birdtradingplatform.service;

public interface EmailService {
    void sendSimpleEmail(String toEmail, String body, String subject);
    boolean isEmailExist(String email);
}
