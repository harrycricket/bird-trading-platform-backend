package com.gangoffive.birdtradingplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class VersionController {

    @GetMapping("/")
    public String getDateRelease() {
        String date = "07/05/2023 19h00";
        String version = "0.2.0 SNAPSHOT";
        return date + " - Version " + version + " - COPYRIGHT © GANG_OF_FIVE - BS2ND";
    }
}
