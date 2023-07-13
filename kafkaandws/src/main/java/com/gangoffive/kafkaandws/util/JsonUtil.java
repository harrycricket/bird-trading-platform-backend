package com.gangoffive.kafkaandws.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.util.StringUtils;

import java.io.IOException;

public enum JsonUtil {
    INSTANCE;

    public String getJsonString(Object object) {
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T getObject(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            throw new IllegalArgumentException("Input argument can not be empty!");
        }
        ObjectMapper om = new ObjectMapper();
        try {
            return om.readValue(jsonString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}