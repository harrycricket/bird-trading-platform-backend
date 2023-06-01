package com.gangoffive.birdtradingplatform.service;

public interface EmailSenderService {
    void sendSimpleEmail(String toEmail, String body, String subject);
}
