package com.gangoffive.birdtradingplatform.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;

@RestController
@Slf4j
public class VersionController {

    @GetMapping("/")
    public String getDateRelease() {
        String date = "08/07/2023 16h00";
        String version = "0.2.1 SNAPSHOT";
        String address = "";
        try {
            // Retrieve the public IP address using an external API
            URL ipApiUrl = new URL("https://api.ipify.org");
            BufferedReader in = new BufferedReader(new InputStreamReader(ipApiUrl.openStream()));
            String ipAddress = in.readLine();
            in.close();
            address = ipAddress;
            log.info("Current IP Address: {}", ipAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return date + " - Version " + version + " - COPYRIGHT © GANG_OF_FIVE - BS2ND - Your Ip Address " + address;
    }
}
