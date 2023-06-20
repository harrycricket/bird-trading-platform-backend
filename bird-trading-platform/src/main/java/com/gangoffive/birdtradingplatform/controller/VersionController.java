package com.gangoffive.birdtradingplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class VersionController {

    @GetMapping("/")
    public String getDateRelease() {
        String date = "20/06/2023 19h25";
        String version = "0.1.1 SNAPSHOT";
        return date + " - Version " + version + " - COPYRIGHT © GANG_OF_FIVE - BS2ND";
    }
}
