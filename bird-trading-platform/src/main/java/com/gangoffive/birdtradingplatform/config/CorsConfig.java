package com.gangoffive.birdtradingplatform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class CorsConfig {
    private final AppProperties appProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(appProperties.getCors().getAllowedOrigins().split(",")).toList());
        configuration.setAllowedMethods(Arrays.stream(appProperties.getCors().getAllowedMethods().split(",")).toList());
        configuration.setAllowedHeaders(Arrays.stream(appProperties.getCors().getAllowedHeaders().split(",")).toList());
        configuration.setExposedHeaders(Arrays.stream(appProperties.getCors().getExposedHeaders().split(",")).toList());
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}
