package com.gangoffive.birdtradingplatform.service;

import org.springframework.http.ResponseEntity;

public interface InfoService {
    ResponseEntity<?> getInfo(String email, String token);
}
