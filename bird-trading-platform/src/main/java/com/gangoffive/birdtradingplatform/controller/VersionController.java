package com.gangoffive.birdtradingplatform.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class VersionController {

    @GetMapping("/")
    public String getDateRelease() {
        String date = "02/06/2023 12h36";
        String version = "0.0.2 SNAPSHOT";
        return date + " - version " + version + " - COPYRIGHT @c GANGOFFIVE - BS2ND";
    }
}
