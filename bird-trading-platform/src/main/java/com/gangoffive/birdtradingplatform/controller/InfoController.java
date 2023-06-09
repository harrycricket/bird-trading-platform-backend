package com.gangoffive.birdtradingplatform.controller;

import com.gangoffive.birdtradingplatform.api.response.ApiError;
import com.gangoffive.birdtradingplatform.service.InfoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/info")
@RequiredArgsConstructor
public class InfoController {
    private final InfoService infoService;

    @GetMapping
    public ResponseEntity<?> getInfo(@RequestParam String token) {
        return infoService.getInfo(token);
    }
}
