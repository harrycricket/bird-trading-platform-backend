package com.gangoffive.birdtradingplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {
    @GetMapping("kafka/test")
    public String getKafka() {
        return "Here is kafka";
    }

}
