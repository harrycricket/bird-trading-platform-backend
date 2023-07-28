package com.gangoffive.birdtradingplatform;

import com.gangoffive.birdtradingplatform.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@EnableScheduling
@EnableAsync
public class BirdTradingPlatformApplication {
	public static void main(String[] args) {
		SpringApplication.run(BirdTradingPlatformApplication.class, args);
	}

}

